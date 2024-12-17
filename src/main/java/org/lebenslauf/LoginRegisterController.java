package org.lebenslauf;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.backend.DBConnect;
import org.backend.User;
import org.backend.util.PasswordUtils;

import java.sql.SQLException;
import java.util.Optional;

public class LoginRegisterController {

    @FXML
    private TextField loginEmailField, registerFullNameField, registerEmailField;

    @FXML
    private PasswordField loginPasswordField, registerPasswordField;

    @FXML
    private Label loginMessage, registerMessage;

    private DBConnect dbConnect;

    public LoginRegisterController(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    @FXML
    private void handleLogin() {
        String email = loginEmailField.getText();
        String password = loginPasswordField.getText();

        // Validate user login
        Task<Optional<User>> loginTask = new Task<>() {
            @Override
            protected Optional<User> call() throws SQLException {
                return dbConnect.getUserByEmail(email);
            }
        };

        loginTask.setOnSucceeded(event -> {
            Optional<User> userOpt = loginTask.getValue();
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (PasswordUtils.checkPassword(password, user.getPassword())) {
                    loginMessage.setText("Login successful!");
                    loginMessage.setTextFill(Color.GREEN);
                    switchToLayout();
                } else {
                    loginMessage.setText("Invalid email or password.");
                    loginMessage.setTextFill(Color.RED);
                }
            } else {
                loginMessage.setText("Invalid email or password.");
                loginMessage.setTextFill(Color.RED);
            }
        });

        loginTask.setOnFailed(event -> {
            Throwable e = loginTask.getException();
            loginMessage.setText("Error: " + e.getMessage());
            loginMessage.setTextFill(Color.RED);
        });

        new Thread(loginTask).start();
    }


    @FXML
    private void handleRegister() {
        String fullName = registerFullNameField.getText();
        String email = registerEmailField.getText();
        String password = registerPasswordField.getText();

        Task<Void> registerTask = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                // Check if email exists
                Optional<User> userOpt = dbConnect.getUserByEmail(email);
                if (userOpt.isPresent()) {
                    Platform.runLater(() -> {
                        registerMessage.setText("Email already exists. Please login.");
                        registerMessage.setTextFill(Color.RED);
                    });
                } else {
                    String hashedPassword = PasswordUtils.hashPassword(password);
                    User newUser = new User(fullName, email, hashedPassword);
                    dbConnect.addUser(newUser);
                    Platform.runLater(() -> {
                        registerMessage.setText("Registration successful! Please login.");
                        registerMessage.setTextFill(Color.GREEN);
                    });
                }
                return null;
            }
        };

        registerTask.setOnFailed(event -> {
            Throwable e = registerTask.getException();
            Platform.runLater(() -> {
                registerMessage.setText("Error: " + e.getMessage());
                registerMessage.setTextFill(Color.RED);
            });
        });

        new Thread(registerTask).start();
    }

    @FXML
    private void switchToLayout() {
        try {
            HelloApplication.loadScene("fxml/main_layout.fxml",dbConnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

