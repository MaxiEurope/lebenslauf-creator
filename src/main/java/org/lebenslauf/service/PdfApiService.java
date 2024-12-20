package org.lebenslauf.service;

import org.lebenslauf.model.Resume;
import org.lebenslauf.util.EnvUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PdfApiService {
    private final String apiKey;

    public PdfApiService() {
        this.apiKey = EnvUtils.getEnv("PDF_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new RuntimeException("API key not found");
        }
    }

    public byte[] generatePdfFromResume(Resume resume) throws IOException {
        String markdownContent = buildMarkdown(resume);

        StringBuilder jsonPayload = new StringBuilder();
        jsonPayload.append("{");
        jsonPayload.append("\"markdown\":\"")
                .append(markdownContent.replace("\"", "\\\"").replace("\n", "\\n"))
                .append("\",");

        jsonPayload.append("\"images\":[");
        if (resume.getImageBase64() != null && !resume.getImageBase64().isEmpty()) {
            String imageBase64 = resume.getImageBase64();
            String imageType = "png";

            if (imageBase64.startsWith("data:")) {
                int mimeStart = imageBase64.indexOf(":") + 1;
                int mimeEnd = imageBase64.indexOf(";");
                if (mimeStart > 0 && mimeEnd > mimeStart) {
                    String mimeType = imageBase64.substring(mimeStart, mimeEnd);
                    if (mimeType.equals("image/jpeg")) {
                        imageType = "jpeg";
                    } else if (mimeType.equals("image/png")) {
                        imageType = "png";
                    }
                }
                imageBase64 = imageBase64.substring(imageBase64.indexOf(",") + 1);
            }

            jsonPayload.append("{").append("\"filename\":\"profile.").append(imageType).append("\",")
                .append("\"data\":\"")
                .append(imageBase64)
                .append("\"")
                .append("}");
        }
        jsonPayload.append("]}");

        HttpURLConnection connection = getHttpURLConnection(jsonPayload);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream responseStream = connection.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = responseStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                return baos.toByteArray();
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                throw new IOException("Error from API: " + response);
            }
        }
    }

    private HttpURLConnection getHttpURLConnection(StringBuilder jsonPayload) throws IOException {
        String payloadString = jsonPayload.toString();
        System.out.println("Payload: " + payloadString);

        String apiUrl = "https://api.maxi-script.com/pdf";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/pdf");
        connection.setRequestProperty("X-API-KEY", apiKey);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payloadString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    private String buildMarkdown(Resume resume) {
        return "# Generated Resume\n\n" +
                "## Personal Information\n" +
                "**Name:** " + resume.getFirstName() + " " + resume.getLastName() + "\n\n" +
                "**Gender:** " + resume.getGender() + "\n\n" +
                "**Birth Place:** " + resume.getBirthPlace() + "\n\n" +
                "**Birth Date:** " + resume.getBirthDate() + "\n\n" +
                "**City:** " + resume.getCity() + "\n\n" +
                "**Address:** " + resume.getAddress() + "\n\n" +
                "**Postal Code:** " + resume.getPostalCode() + "\n\n" +
                "**Nationality:** " + resume.getNationality() + "\n\n" +
                "**Phone:** " + resume.getPhoneNumber() + "\n\n" +
                "**Email:** " + resume.getEmail() + "\n\n" +
                "## Experience\n" +
                (resume.getExperience() != null ? String.join("\n- ", resume.getExperience()) : "") + "\n\n" +
                "## Education\n" +
                (resume.getEducation() != null ? String.join("\n- ", resume.getEducation()) : "") + "\n\n" +
                "---\n\n" +
                "![Profile](profile.png \"pfp\")";
    }
}
