package org.lebenslauf;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.backend.DBConnect;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

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

        try {
            // Validate user login
            if (validateUser(email, hashPassword(password))) {
                loginMessage.setText("Login successful!");
                loginMessage.setTextFill(Color.GREEN);
                switchToLayout();
            } else {
                loginMessage.setText("Invalid email or password.");
                loginMessage.setTextFill(Color.RED);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            loginMessage.setText("Error: " + e.getMessage());
            loginMessage.setTextFill(Color.RED);
        }
    }


    @FXML
    private void handleRegister() {
        String fullName = registerFullNameField.getText();
        String email = registerEmailField.getText();
        String password = registerPasswordField.getText();

        try {
            // Check if email exists
            if (checkEmailExists(email)) {
                registerMessage.setText("Email already exists. Please login.");
                registerMessage.setTextFill(Color.RED);
            } else {
                // Hash the password and register the user
                dbConnect.addUser(fullName, email, hashPassword(password));
                registerMessage.setText("Registration successful! Please login.");
                registerMessage.setTextFill(Color.GREEN);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            registerMessage.setText("Error: " + e.getMessage());
            registerMessage.setTextFill(Color.RED);
        }
    }

    @FXML
    private void switchToLayout() {
        try {
            HelloApplication.loadScene("layout.fxml",dbConnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateUser(String email, String hashedPassword) throws SQLException {
        return dbConnect.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equals(email) && user.getPassword().equals(hashedPassword));
    }

    private boolean checkEmailExists(String email) throws SQLException {
        return dbConnect.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
}

