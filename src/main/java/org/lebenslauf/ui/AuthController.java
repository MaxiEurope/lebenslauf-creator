package org.lebenslauf.ui;

import org.lebenslauf.app.MainApp;
import org.lebenslauf.model.User;
import org.lebenslauf.service.UserService;
import org.lebenslauf.service.ResumeService;
import org.lebenslauf.util.PasswordUtils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import java.util.Optional;

public class AuthController {

    @FXML
    private TextField loginEmailField, registerFullNameField, registerEmailField;

    @FXML
    private PasswordField loginPasswordField, registerPasswordField;

    @FXML
    private Label loginMessage, registerMessage;

    private User loggedInUser;

    private final UserService userService;

    // TODO: load resume (from quick saving)
    private final ResumeService resumeService;

    public AuthController(UserService userService, ResumeService resumeService) {
        this.userService = userService;
        this.resumeService = resumeService;
    }

    @FXML
    private void handleLogin() {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        // Validate user login
        Task<Optional<User>> loginTask = new Task<>() {
            @Override
            protected Optional<User> call() {
                return userService.validateCredentials(email, PasswordUtils.hashPassword(password));
            }
        };

        loginTask.setOnSucceeded(e -> {
            Optional<User> user = loginTask.getValue();
            if (user.isPresent()) {
                loggedInUser = user.get();
                loginMessage.setText("Login successful!");
                loginMessage.setTextFill(Color.GREEN);
                switchToMainLayout();
            } else {
                loginMessage.setText("Invalid email or password.");
                loginMessage.setTextFill(Color.RED);
            }
        });

        loginTask.setOnFailed(e -> {
            loginMessage.setText("Error: " + loginTask.getException().getMessage());
            loginMessage.setTextFill(Color.RED);
        });

        new Thread(loginTask).start();
    }


    @FXML
    private void handleRegister() {
        String fullName = registerFullNameField.getText();
        String email = registerEmailField.getText();
        String password = registerPasswordField.getText();

        Task<Boolean> registerTask = new Task<>() {
            @Override
            protected Boolean call() {
                if (userService.emailExists(email)) {
                    return false;
                } else {
                    User user = new User(0, fullName, email, PasswordUtils.hashPassword(password));
                    userService.addUser(user);
                    return true;
                }
            }
        };

        registerTask.setOnSucceeded(e -> {
            if (registerTask.getValue()) {
                registerMessage.setText("Registration successful! Please login.");
                registerMessage.setTextFill(Color.GREEN);
            } else {
                registerMessage.setText("Email already exists. Please login.");
                registerMessage.setTextFill(Color.RED);
            }
        });

        registerTask.setOnFailed(e -> {
            registerMessage.setText("Error: " + registerTask.getException().getMessage());
            registerMessage.setTextFill(Color.RED);
        });

        new Thread(registerTask).start();
    }

    private void switchToMainLayout() {
        Platform.runLater(() -> {
            try {
                MainApp.loadScene("main_layout.fxml", "Resume Editor", loggedInUser);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}

