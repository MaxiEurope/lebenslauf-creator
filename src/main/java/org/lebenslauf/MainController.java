package org.lebenslauf;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Arrays;

public class MainController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField genderField;
    @FXML
    private TextField birthPlaceField;
    @FXML
    private TextField birthDateField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea experienceField;
    @FXML
    private TextArea educationField;

    @FXML
    private void handleSubmit() {
        Resume resume = new Resume();
        resume.setFirstName(firstNameField.getText());
        resume.setLastName(lastNameField.getText());
        resume.setGender(genderField.getText());
        resume.setBirthPlace(birthPlaceField.getText());
        resume.setBirthDate(birthDateField.getText());
        resume.setCity(cityField.getText());
        resume.setAddress(addressField.getText());
        resume.setPostalCode(postalCodeField.getText());
        resume.setPhoneNumber(phoneNumberField.getText());
        resume.setEmail(emailField.getText());
        resume.setExperience(Arrays.asList(experienceField.getText().split("\\n")));
        resume.setEducation(Arrays.asList(educationField.getText().split("\\n")));

        System.out.println("Resume Data: " + resume.toString());
    }
}
