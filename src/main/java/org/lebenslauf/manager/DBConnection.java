package org.lebenslauf.manager;

import org.lebenslauf.util.EnvUtils;
import org.lebenslauf.util.DialogUtils;
import org.lebenslauf.util.LogUtils;

import java.sql.*;

/**
 * Manage the connection to the MySQL database.
 * Uses env variables for database credentials.
 */
public class DBConnection {

    // Database credentials
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/lebenslauf-creator";
    private static final String USER = EnvUtils.getEnv("DB_USER"); // MySQL username
    private static final String PASS = EnvUtils.getEnv("DB_PASS"); // MySQL password

    private final String dbUrl;

    // Connection object
    private Connection conn = null;

    public DBConnection() {
        this(DEFAULT_DB_URL, USER, PASS);
    }

    /**
     * DBConnection instance.
     *
     * @param customDbUrl the custom database URL to use. Uses the default database URL if not provided.
     * @param user the username
     * @param pass the password
     * @throws IllegalArgumentException if 'DB_USER' is missing or empty
     * @throws IllegalArgumentException if 'DB_PASS' is missing or empty
     */
    public DBConnection(String customDbUrl, String user, String pass) {
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

        this.dbUrl = (customDbUrl == null || customDbUrl.isEmpty()) ? DEFAULT_DB_URL : customDbUrl;
    }

    
    /**
     * Establishes a connection to the MySQL database.
     *
     * @return true if the connection is successful.
     * @throws SQLException if a database access error occurs.
     */
    public boolean connect() {
        try {
            conn = DriverManager.getConnection(dbUrl, USER, PASS);
            LogUtils.logInfo("Connected to MySQL database!" + conn);
            return true;
        } catch (SQLException e) {
            LogUtils.logError(e, "Could not connect to the MySQL database: " + e.getMessage());
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

    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        try {
            if (conn != null) { conn.close(); }
            LogUtils.logInfo("Disconnected from the database.");
        } catch (SQLException se) {
            LogUtils.logError(se, "Error disconnecting from database: " + se.getMessage());
            DialogUtils.showErrorDialog(
                "Error disconnecting from database:\n" + se.getMessage(),
                "Database Disconnect Error"
            );
            throw new RuntimeException("Error disconnecting from database: " + se.getMessage());
        }
    }
}
