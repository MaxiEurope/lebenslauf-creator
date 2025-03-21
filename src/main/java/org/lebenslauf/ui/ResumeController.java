package org.lebenslauf.ui;

import org.lebenslauf.model.User;
import org.lebenslauf.model.Resume;
import org.lebenslauf.service.UserService;
import org.lebenslauf.service.ResumeService;
import org.lebenslauf.service.PdfApiService;
import org.lebenslauf.util.DialogUtils;
import org.lebenslauf.util.LogUtils;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.beans.value.ChangeListener;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller class for the resume layout.
 */
public class ResumeController {
    @FXML
    private TextField firstNameField, lastNameField, birthPlaceField, cityField, addressField, postalCodeField, phoneNumberField, emailField;
    @FXML
    private ComboBox<String> genderComboBox, nationalityComboBox, fontSizeComboBox, fontColorComboBox, fontFamilyComboBox, themeComboBox;
    @FXML
    private DatePicker birthDateField;
    @FXML
    private TextArea experienceField, educationField;
    @FXML
    private Label imagePathLabel, confirmationLabel;
    @FXML
    private Button submitButton;
    @FXML
    private ComboBox<String> translationLanguageComboBox;
    @FXML
    private WebView pdfPreviewWebView;
    @FXML
    private Button quickSaveButton, quickLoadButton;

    private boolean isDarkMode = false;
    private final String DARK_MODE_CSS = getClass().getResource("/org/lebenslauf/css/dark.css").toExternalForm();
    private final String LIGHT_MODE_CSS = getClass().getResource("/org/lebenslauf/css/light.css").toExternalForm();

    @FXML
    private Parent root;

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

    /**
     * Initializes the controller with default values and listeners.
     * 
     * A timeline is set up to update the preview every 5 seconds if the resume has changed.
     */
    @FXML
    private void initialize() {
        genderComboBox.getItems().addAll("Männlich", "Weiblich", "Divers");
        genderComboBox.setValue("Choose");
        nationalityComboBox.getItems().addAll("Deutsch", "Österreichisch", "Schweizerisch", "Andere");
        nationalityComboBox.setValue("Choose");
        birthDateField.getEditor().setDisable(true);
        birthDateField.getEditor().setOpacity(1);

        translationLanguageComboBox.getItems().addAll("None", "DE", "EN");
        translationLanguageComboBox.setValue("None");

        fontSizeComboBox.getItems().addAll("10", "12", "14", "16");
        fontSizeComboBox.setValue("12");
        fontColorComboBox.getItems().addAll("Schwarz", "Weiß", "Rot", "Blau");
        fontColorComboBox.setValue("Schwarz");
        fontFamilyComboBox.getItems().addAll("Arial", "Times New Roman", "Courier New", "Verdana");
        fontFamilyComboBox.setValue("Arial");
        themeComboBox.getItems().addAll("1", "2", "3", "4");
        themeComboBox.setValue("1");

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

        // Save the resume as a quicksave
        @FXML
        private void handleQuickSave() {
            Resume resume = getResume(); // Get current resume from fields
            resumeService.quickSaveResume(loggedInUser.getId(), resume); // Save it using the ResumeService
            DialogUtils.showInfoDialog("QuickSave completed!", "Info");
        }

            // Load the quicksaved resume into the form
    @FXML
    private void handleQuickLoad() {
        Resume loadedResume = resumeService.loadQuickSavedResume(loggedInUser.getId());
        if (loadedResume != null) {
            // Fill in the form with the loaded resume data
            firstNameField.setText(loadedResume.getFirstName());
            lastNameField.setText(loadedResume.getLastName());
            birthPlaceField.setText(loadedResume.getBirthPlace());
            cityField.setText(loadedResume.getCity());
            addressField.setText(loadedResume.getAddress());
            postalCodeField.setText(loadedResume.getPostalCode());
            phoneNumberField.setText(loadedResume.getPhoneNumber());
            emailField.setText(loadedResume.getEmail());
            List<String> eduList = loadedResume.getEducation();
            List<String> expList = loadedResume.getExperience();
            String educationText = String.join("\n", eduList);
            String expirienceText = String.join("\n", expList);
            educationField.setText(educationText);
            experienceField.setText(expirienceText);
            imageBase64 = loadedResume.getImageBase64();
            imagePathLabel.setText("Loaded from quick save");
            resumeChanged.set(true);
            
;
        } else {
            DialogUtils.showErrorDialog("No QuickSaved resume found for this user.", "Error");
        }
    }

    /**
     * Toggles the theme of the application.
     */
    @FXML
    private void handleToggleTheme() {
        Scene scene = root.getScene();
        if (isDarkMode) {
            scene.getStylesheets().remove(DARK_MODE_CSS);
            scene.getStylesheets().add(LIGHT_MODE_CSS);
        } else {
            scene.getStylesheets().remove(LIGHT_MODE_CSS);
            scene.getStylesheets().add(DARK_MODE_CSS);
        }
        isDarkMode = !isDarkMode;
    }

    /**
     * Sets up listeners for the input fields so that the resumeChanged flag is set to true when the resume is modified.
     * 
     * The flag is used to update the preview every 5 seconds if the resume has changed.
     */
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
        fontSizeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        fontColorComboBox.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        fontFamilyComboBox.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        themeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        birthDateField.valueProperty().addListener((obs, oldVal, newVal) -> resumeChanged.set(true));
        quickSaveButton.setOnAction(event -> handleQuickSave());
        quickLoadButton.setOnAction(event -> handleQuickLoad());
    }

    /**
     * Handles the image upload button click event.
     * 
     * Opens a file chooser dialog to select an image file (jpg, jpeg, png).
     * The image is read and converted to a base64 string.
     */
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
            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                byte[] bytes = fis.readAllBytes();

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

                LogUtils.logInfo("Image Base64 str: " + imageBase64); // debug
                resumeChanged.set(true);
            } catch (IOException e) {
                LogUtils.logError(e, "Error reading image file");
                imagePathLabel.setText("Error reading file");
                DialogUtils.showErrorDialog("Unable to read image file.\n" + e.getMessage(), "File I/O Error");
            } catch (IllegalArgumentException e) {
                LogUtils.logError(e, "Unsupported file type");
                imagePathLabel.setText("Unsupported file type");
                DialogUtils.showErrorDialog(e.getMessage(), "Image Upload Error");
            }
        } else {
            imagePathLabel.setText("No file selected");
        }
    }

    /**
     * Handles the submit button click event.
     * 
     * Validates the input fields and saves the resume data to the database.
     * A PDF is generated from the resume and saved to the filesystem.
     * 
     * Thread to save the resume data and to avoid freezing the UI.
     */
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

        LogUtils.logInfo("Resume Data: " + resume.toString()); // debug

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
            LogUtils.logError(ex, "Error saving resume");
            confirmationLabel.setText("Error saving resume: " + ex.getMessage());
            DialogUtils.showErrorDialog(
                "Error saving resume:\n" + ex.getMessage(),
                "Resume Saving Error"
            );
            submitButton.setDisable(false);
        });

        new Thread(saveResumeTask).start();
    }

    /**
     * Creates a Resume object from the input fields.
     * 
     * @return the Resume object
     */
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

        resume.setFontColor(fontColorComboBox.getValue() != null ? fontColorComboBox.getValue() : "Schwarz");
        resume.setFontSize(fontSizeComboBox.getValue() != null ? fontSizeComboBox.getValue() : "12");
        resume.setFontFamily(fontFamilyComboBox.getValue() != null ? fontFamilyComboBox.getValue() : "Arial");
        resume.setTheme(themeComboBox.getValue() != null ? themeComboBox.getValue() : "1");

        List<String> expList = Arrays.asList(experienceField.getText().split("\\n"));
        resume.setExperience(expList);
        List<String> eduList = Arrays.asList(educationField.getText().split("\\n"));
        resume.setEducation(eduList);

        resume.setImageBase64(imageBase64);
        return resume;
    }

    /**
     * Generates a PDF from the resume data.
     * 
     * The PDF is saved to the filesystem.
     * 
     * @param resume the Resume object
     */
    private void generatePdfFromResume(Resume resume) {
        Task<byte[]> pdfTask = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                String selectedLanguage = translationLanguageComboBox.getValue();
                return pdfApiService.generatePdfFromResume(resume, selectedLanguage, resume.getFontSize(), resume.getFontColor(), resume.getFontFamily(), resume.getTheme());
            }
        };

        pdfTask.setOnSucceeded(e -> {
            byte[] pdfContent = pdfTask.getValue();

            String outputFileName = "resume_" + resume.getFirstName() + "_" + resume.getLastName() + ".pdf";

            try(FileOutputStream fos = new FileOutputStream(outputFileName)) {
                fos.write(pdfContent);
                confirmationLabel.setText("PDF generated and saved as " + outputFileName);
            } catch (IOException ex) {
                LogUtils.logError(ex, "Error writing PDF file");
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
            LogUtils.logError(ex, "Error generating PDF");
            confirmationLabel.setText("Error generating PDF: " + ex.getMessage());
            DialogUtils.showErrorDialog(
                "Error generating PDF:\n" + ex.getMessage(),
                "PDF Generation Error"
            );
            submitButton.setDisable(false);
        });

        new Thread(pdfTask).start();
    }

    /**
     * Updates the HTML preview of the resume.
     * 
     * The preview is updated with the latest resume data.
     */
    private void updatePreview() {
        Resume resume = getResume();

        Task<String> previewTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                String selectedLanguage = translationLanguageComboBox.getValue();
                return pdfApiService.generateHtmlFromResume(resume, selectedLanguage, resume.getFontSize(), resume.getFontColor(), resume.getFontFamily(), resume.getTheme());
            }
        };

        previewTask.setOnSucceeded(event -> {
            String htmlContent = previewTask.getValue();
            pdfPreviewWebView.getEngine().loadContent(htmlContent, "text/html");
            resumeChanged.set(false);
        });

        previewTask.setOnFailed(event -> {
            Throwable ex = previewTask.getException();
            LogUtils.logError(ex, "Error generating HTML preview");
            DialogUtils.showErrorDialog(
                "Error generating HTML preview:\n" + ex.getMessage(),
                "Preview Error"
            );
            resumeChanged.set(false);
        });

        new Thread(previewTask).start();
    }
}
