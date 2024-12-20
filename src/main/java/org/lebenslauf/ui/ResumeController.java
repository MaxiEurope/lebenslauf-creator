package org.lebenslauf.ui;

import org.lebenslauf.model.User;
import org.lebenslauf.model.Resume;
import org.lebenslauf.service.UserService;
import org.lebenslauf.service.ResumeService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.concurrent.Task;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ResumeController {
    @FXML
    private TextField firstNameField, lastNameField, birthPlaceField, cityField, addressField, postalCodeField, phoneNumberField, emailField;
    @FXML
    private ComboBox<String> genderComboBox, nationalityComboBox;
    @FXML
    private DatePicker birthDateField;
    @FXML
    private TextArea experienceField, educationField;
    @FXML
    private Label imagePathLabel, confirmationLabel;

    private String imageBase64;
    private final UserService userService;
    private final ResumeService resumeService;
    private final User loggedInUser;

    public ResumeController(UserService userService, ResumeService resumeService, User loggedInUser) {
        this.userService = userService;
        this.resumeService = resumeService;
        this.loggedInUser = loggedInUser;
    }

    @FXML private void initialize() {
        genderComboBox.getItems().addAll("Männlich", "Weiblich", "Divers");
        nationalityComboBox.getItems().addAll("Deutsch", "Österreichisch", "Schweizerisch", "Andere");
    }

    @FXML
    private void handleImageUpload() {
        Window window = imagePathLabel.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            imagePathLabel.setText(selectedFile.getAbsolutePath());
            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                byte[] bytes = fis.readAllBytes();
                fis.close();

                String fileName = selectedFile.getName().toLowerCase();
                String mimeType;
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    mimeType = "image/jpeg";
                } else if (fileName.endsWith(".png")) {
                    mimeType = "image/png";
                } else {
                    throw new IllegalArgumentException("Unsupported file type");
                }

                imageBase64 = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);

                System.out.println("Image Base64 String: " + imageBase64); // debug
            } catch (IOException e) {
                e.printStackTrace();
                imagePathLabel.setText("Error reading file");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imagePathLabel.setText("Unsupported file type");
            }
        } else {
            imagePathLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSubmit() {
        Resume resume = new Resume();
        resume.setFirstName(firstNameField.getText());
        resume.setLastName(lastNameField.getText());
        resume.setGender(genderComboBox.getValue());
        resume.setBirthPlace(birthPlaceField.getText());
        LocalDate date = birthDateField.getValue();
        resume.setBirthDate(date != null ? date.toString() : "");
        resume.setCity(cityField.getText());
        resume.setAddress(addressField.getText());
        resume.setPostalCode(postalCodeField.getText());
        resume.setNationality(nationalityComboBox.getValue());
        resume.setPhoneNumber(phoneNumberField.getText());
        resume.setEmail(emailField.getText());
        List<String> expList = Arrays.asList(experienceField.getText().split("\\n"));
        resume.setExperience(expList);
        List<String> eduList = Arrays.asList(educationField.getText().split("\\n"));
        resume.setEducation(eduList);
        resume.setImageBase64(imageBase64);

        System.out.println("Resume Data: " + resume.toString()); // debug

        int loggedInUserId = loggedInUser.getId();

        Task<Void> saveResumeTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int newVersionNumber = resumeService.getNextVersionNumber(loggedInUserId);
                resumeService.saveResumeVersion(loggedInUserId, newVersionNumber, resume);
                return null;
            }
        };

        saveResumeTask.setOnSucceeded(e -> {
            confirmationLabel.setText("Resume saved successfully!");
        });

        saveResumeTask.setOnFailed(e -> {
            confirmationLabel.setText("Error saving resume: " + saveResumeTask.getException().getMessage());
        });

        new Thread(saveResumeTask).start();
    }
}
