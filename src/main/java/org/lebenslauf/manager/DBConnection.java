package org.lebenslauf.manager;

import java.sql.*;

public class DBConnection {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lebenslauf-creator";
    private static final String USER = "root"; // MySQL username
    private static final String PASS = ""; // MySQL password

    // Connection object
    private Connection conn = null;

    // Method to establish the connection
    public boolean connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to MySQL database!" + conn);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
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
        }
    }
}
