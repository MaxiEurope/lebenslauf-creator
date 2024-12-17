package org.backend;
import java.sql.*;
import java.util.*;

public class DBConnect {

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


    // Retrieve user data by ID
    public User getUserById(int id) {
        User user = null;
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("Full_Name"),
                            rs.getString("Email"),
                            rs.getString("Password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    // Add a new user
    public void addUser(User user) {
        String query = "INSERT INTO users (Full_Name, Email, Password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update user information
    public void updateUser(User user) {
        String query = "UPDATE users SET Full_Name = ?, Email = ?, Password = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE Email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getInt("id"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Password")
                );
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Retrieve all PDFs
//    public List<PDF> getAllPDFs() throws SQLException {
//        List<PDF> pdfs = new ArrayList<>();
//        String query = "SELECT * FROM pdf";
//
//        try (
//             PreparedStatement statement = conn.prepareStatement(query);
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                PDF pdf = new PDF(
//                        resultSet.getInt("id"),
//                        resultSet.getInt("user_id"),
//                        resultSet.getString("pdf_location")
//                );
//                pdfs.add(pdf);
//            }
//        }
//        return pdfs;
//    }
//
//    // Add a new PDF
//    public void addPDF(int userId, String pdfLocation) throws SQLException {
//        String query = "INSERT INTO pdf (user_id, pdf_location) VALUES (?, ?)";
//
//        try (
//             PreparedStatement statement = conn.prepareStatement(query)) {
//
//            statement.setInt(1, userId);
//            statement.setString(2, pdfLocation);
//            statement.executeUpdate();
//        }
//    }
//
//    // Update a PDF
//    public void updatePDF(int id, int userId, String pdfLocation) throws SQLException {
//        String query = "UPDATE pdf SET user_id = ?, pdf_location = ? WHERE id = ?";
//
//        try (
//             PreparedStatement statement = conn.prepareStatement(query)) {
//
//            statement.setInt(1, userId);
//            statement.setString(2, pdfLocation);
//            statement.setInt(3, id);
//            statement.executeUpdate();
//        }
//    }
//
//    // Retrieve PDF data by user ID
//    public List<PDF> getPDFsByUserId(int userId) {
//        List<PDF> pdfs = new ArrayList<>();
//        try {
//            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pdf WHERE user_id = ?");
//            stmt.setInt(1, userId);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                PDF pdf = new PDF(rs.getInt("id"), rs.getInt("user_id"), rs.getString("pdf_location"));
//                pdfs.add(pdf);
//            }
//            rs.close();
//            stmt.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return pdfs;
//    }
//
//    // Add a new PDF
//    public void addPDF(PDF pdf) {
//        try {
//            PreparedStatement stmt = conn.prepareStatement("INSERT INTO pdf (user_id, pdf_location) VALUES (?, ?)");
//            stmt.setInt(1, pdf.getUserId());
//            stmt.setString(2, pdf.getPdfLocation());
//            stmt.executeUpdate();
//            stmt.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void disconnect() {
        try {
            if (conn != null) conn.close();
            System.out.println("Disconnected from the database.");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
