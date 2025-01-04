package org.lebenslauf.ui;

import org.lebenslauf.model.User;
import org.lebenslauf.model.Resume;
import org.lebenslauf.service.UserService;
import org.lebenslauf.service.ResumeService;
import org.lebenslauf.service.PdfApiService;
import org.lebenslauf.util.DialogUtils;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    @FXML
    private Button submitButton;

    @FXML
    private WebView pdfPreviewWebView;

    private String imageBase64;
    private final UserService userService;
    private final ResumeService resumeService;
    private final PdfApiService pdfApiService = new PdfApiService();
    private final User loggedInUser;

    private final AtomicBoolean resumeChanged = new AtomicBoolean(false);

    public ResumeController(UserService userService, ResumeService resumeService, User loggedInUser) {
        this.userService = userService;
        this.resumeService = resumeService;
        this.loggedInUser = loggedInUser;
    }

    @FXML private void initialize() {
        genderComboBox.getItems().addAll("Männlich", "Weiblich", "Divers");
        nationalityComboBox.getItems().addAll("Deutsch", "Österreichisch", "Schweizerisch", "Andere");

        setupListeners();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> {
                if (resumeChanged.get()) {
                    updatePreview();
                }
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupListeners() {
        ChangeListener<String> textChangeListener = (observable, oldValue, newValue) -> resumeChanged.set(true);

        firstNameField.textProperty().addListener(textChangeListener);
        lastNameField.textProperty().addListener(textChangeListener);
        birthPlaceField.textProperty().addListener(textChangeListener);
        cityField.textProperty().addListener(textChangeListener);
        addressField.textProperty().addListener(textChangeListener);
        postalCodeField.textProperty().addListener(textChangeListener);
        phoneNumberField.textProperty().addListener(textChangeListener);
        emailField.textProperty().addListener(textChangeListener);
        experienceField.textProperty().addListener(textChangeListener);
        educationField.textProperty().addListener(textChangeListener);

        genderComboBox.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        nationalityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        birthDateField.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
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

                resumeChanged.set(true);
            } catch (IOException e) {
                e.printStackTrace();
                imagePathLabel.setText("Error reading file");
                DialogUtils.showErrorDialog("Unable to read image file.\n" + e.getMessage(), "File I/O Error");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imagePathLabel.setText("Unsupported file type");
                DialogUtils.showErrorDialog(e.getMessage(), "Image Upload Error");
            }
        } else {
            imagePathLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSubmit() {
        if (firstNameField.getText() == null || firstNameField.getText().trim().isEmpty()) {
            DialogUtils.showErrorDialog("First name is required.", "Validation Error");
            return;
        }
        if (lastNameField.getText() == null || lastNameField.getText().trim().isEmpty()) {
            DialogUtils.showErrorDialog("Last name is required.", "Validation Error");
            return;
        }
        if (emailField.getText() == null || !emailField.getText().contains("@")) {
            DialogUtils.showErrorDialog("Please enter a valid email address.", "Validation Error");
            return;
        }

        Resume resume = getResume();

        System.out.println("Resume Data: " + resume.toString()); // debug

        int loggedInUserId = loggedInUser.getId();

        submitButton.setDisable(true);

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
            generatePdfFromResume(resume);
        });

        saveResumeTask.setOnFailed(e -> {
            Throwable ex = saveResumeTask.getException();
            ex.printStackTrace();
            confirmationLabel.setText("Error saving resume: " + ex.getMessage());
            DialogUtils.showErrorDialog(
                "Error saving resume:\n" + ex.getMessage(),
                "Resume Saving Error"
            );
            submitButton.setDisable(false);
        });

        new Thread(saveResumeTask).start();
    }

    private Resume getResume() {
        Resume resume = new Resume();
        resume.setFirstName(firstNameField.getText());
        resume.setLastName(lastNameField.getText());
        resume.setGender(genderComboBox.getValue() != null ? genderComboBox.getValue() : "N/A");
        resume.setBirthPlace(birthPlaceField.getText());
        LocalDate date = birthDateField.getValue();
        resume.setBirthDate(date != null ? date.toString() : "");
        resume.setCity(cityField.getText());
        resume.setAddress(addressField.getText());
        resume.setPostalCode(postalCodeField.getText());
        resume.setNationality(nationalityComboBox.getValue() != null ? nationalityComboBox.getValue() : "N/A");
        resume.setPhoneNumber(phoneNumberField.getText());
        resume.setEmail(emailField.getText());

        List<String> expList = Arrays.asList(experienceField.getText().split("\\n"));
        resume.setExperience(expList);
        List<String> eduList = Arrays.asList(educationField.getText().split("\\n"));
        resume.setEducation(eduList);

        resume.setImageBase64(imageBase64);
        return resume;
    }

    private void generatePdfFromResume(Resume resume) {
        Task<byte[]> pdfTask = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                return pdfApiService.generatePdfFromResume(resume);
            }
        };

        pdfTask.setOnSucceeded(e -> {
            byte[] pdfContent = pdfTask.getValue();

            String outputFileName = "resume_" + resume.getFirstName() + "_" + resume.getLastName() + ".pdf";

            try(FileOutputStream fos = new FileOutputStream(outputFileName)) {
                fos.write(pdfContent);
                confirmationLabel.setText("PDF generated and saved as " + outputFileName);
            } catch (IOException ex) {
                ex.printStackTrace();
                confirmationLabel.setText("Error writing PDF file: " + ex.getMessage());
                DialogUtils.showErrorDialog(
                    "Error writing PDF file:\n" + ex.getMessage(),
                    "File I/O Error"
                );
            } finally {
                submitButton.setDisable(false);
            }
        });

        pdfTask.setOnFailed(e -> {
            Throwable ex = pdfTask.getException();
            ex.printStackTrace();
            confirmationLabel.setText("Error generating PDF: " + ex.getMessage());
            DialogUtils.showErrorDialog(
                "Error generating PDF:\n" + ex.getMessage(),
                "PDF Generation Error"
            );
            submitButton.setDisable(false);
        });

        new Thread(pdfTask).start();
    }

    private void updatePreview() {
        Resume resume = getResume();

        Task<String> previewTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return pdfApiService.generateHtmlFromResume(resume);
            }
        };

        previewTask.setOnSucceeded(event -> {
            String htmlContent = previewTask.getValue();

            pdfPreviewWebView.getEngine().loadContent(htmlContent, "text/html");

            resumeChanged.set(false);
        });

        previewTask.setOnFailed(event -> {
            Throwable ex = previewTask.getException();
            ex.printStackTrace();
            DialogUtils.showErrorDialog(
                "Error generating HTML preview:\n" + ex.getMessage(),
                "Preview Error"
            );
            resumeChanged.set(false);
        });

        new Thread(previewTask).start();
    }
}
