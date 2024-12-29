package org.lebenslauf.manager;

import org.lebenslauf.util.EnvUtils;
import org.lebenslauf.util.DialogUtils;

import java.sql.*;

public class DBConnection {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lebenslauf-creator";
    private static final String USER = EnvUtils.getEnv("DB_USER"); // MySQL username
    private static final String PASS = EnvUtils.getEnv("DB_PASS"); // MySQL password

    // Connection object
    private Connection conn = null;

    public DBConnection() {
        if (USER == null || USER.isEmpty()) {
            DialogUtils.showErrorDialog(
                "Environment variable 'DB_USER' is missing or empty. Please set it in the environment.",
                "Configuration Error"
            );
            throw new IllegalArgumentException("Environment variable 'DB_USER' is missing or empty.");
        }

        if (PASS == null) { // || PASS.isEmpty()
            DialogUtils.showErrorDialog(
                "Environment variable 'DB_PASS' is missing or empty. Please set it in the environment.",
                "Configuration Error"
            );
            throw new IllegalArgumentException("Environment variable 'DB_PASS' is missing or empty.");
        }
    }

    // Method to establish the connection
    public boolean connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to MySQL database!" + conn);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            DialogUtils.showErrorDialog(
                "Could not connect to the MySQL database.\n" + e.getMessage(),
                "Database Connection Error"
            );
            return false;
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void disconnect() {
        try {
            if (conn != null) { conn.close(); }
            System.out.println("Disconnected from the database.");
        } catch (SQLException se) {
            se.printStackTrace();
            DialogUtils.showErrorDialog(
                "Error disconnecting from database:\n" + se.getMessage(),
                "Database Disconnect Error"
            );
        }
    }
}
