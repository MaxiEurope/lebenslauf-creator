package org.backend;
import java.sql.*;

public class DBConnect {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lebenslauf-creator";
    private static final String USER = "root"; // MySQL username
    private static final String PASS = ""; // MySQL password

    // Connection object
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement pstmt = null;

    // Method to establish the connection
    public boolean connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to MySQL database!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to execute SQL queries (CREATE, INSERT, UPDATE, DELETE)
    public boolean executeSQL(String sql) {
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to insert data using PreparedStatement
    public boolean insertData(String sql, String name, int age) {
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void queryData(String sql) {
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            if (stmt != null) stmt.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
            System.out.println("Disconnected from the database.");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
