package org.lebenslauf;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class MainController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField birthPlaceField;
    @FXML
    private DatePicker birthDateField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private ComboBox<String> nationalityComboBox;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea experienceField;
    @FXML
    private TextArea educationField;

    @FXML
    private Label imagePathLabel;

    private String imageBase64;

    @FXML private void initialize() {
        genderComboBox.getItems().addAll("Männlich", "Weiblich", "Divers");
        nationalityComboBox.getItems().addAll("Deutsch", "Österreichisch", "Schweizerisch", "Andere");
    }

    @FXML
    private void handleImageUpload() {
        Window window = imagePathLabel.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            imagePathLabel.setText(selectedFile.getAbsolutePath());

            try {
                FileInputStream fis = new FileInputStream(selectedFile);
                byte[] bytes = fis.readAllBytes();
                fis.close();

                imageBase64 = Base64.getEncoder().encodeToString(bytes);

                System.out.println("Image Base64 String: " + imageBase64);

            } catch (IOException e) {
                e.printStackTrace();
                imagePathLabel.setText("Error reading file");
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
        resume.setGender(String.valueOf(genderComboBox.getValue()));
        resume.setBirthPlace(birthPlaceField.getText());
        resume.setBirthDate(String.valueOf(birthDateField.getValue()));
        resume.setCity(cityField.getText());
        resume.setAddress(addressField.getText());
        resume.setPostalCode(postalCodeField.getText());
        resume.setNationality(String.valueOf(nationalityComboBox.getValue()));
        resume.setPhoneNumber(phoneNumberField.getText());
        resume.setEmail(emailField.getText());
        resume.setExperience(Arrays.asList(experienceField.getText().split("\\n")));
        resume.setEducation(Arrays.asList(educationField.getText().split("\\n")));

        System.out.println("Resume Data: " + resume.toString());
    }
}
