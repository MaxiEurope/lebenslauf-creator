package org.lebenslauf.service;

import org.lebenslauf.model.Resume;
import org.lebenslauf.util.EnvUtils;
import org.lebenslauf.util.DialogUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class PdfApiService {
    private final String apiKey;

    public PdfApiService() {
        this.apiKey = EnvUtils.getEnv("PDF_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            DialogUtils.showErrorDialog(
                "PDF_API_KEY is missing or empty in the environment variables.",
                "API Key Error"
            );

            throw new RuntimeException("API key not found");
        }
    }

    public byte[] generatePdfFromResume(Resume resume) throws IOException, URISyntaxException {
        String jsonPayload = buildJsonPayload(resume);

        HttpURLConnection connection = openApiConnection(jsonPayload, false);

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
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                throw new IOException("Error from API (PDF): " + response);
            }
        }
    }

    public String generateHtmlFromResume(Resume resume) throws IOException, URISyntaxException {
        String jsonPayload = buildJsonPayload(resume);

        HttpURLConnection connection = openApiConnection(jsonPayload, true);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");
                }
                return response.toString();
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                throw new IOException("Error from API (HTML): " + response);
            }
        }
    }

    private HttpURLConnection openApiConnection(String jsonPayload, boolean requestHtml) throws IOException, URISyntaxException {
        String apiUrl = "https://api.maxi-script.com/pdf";
        URI uri = new URI(apiUrl);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        if (requestHtml) {
            connection.setRequestProperty("Accept", "text/html");
        } else {
            connection.setRequestProperty("Accept", "application/pdf");
        }
        connection.setRequestProperty("X-API-KEY", apiKey);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

    private String buildJsonPayload(Resume resume) {
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

        return jsonPayload.toString();
    }

    private String buildMarkdown(Resume resume) {
        return "# Resume\n\n" +
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
