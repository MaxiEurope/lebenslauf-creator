package org.lebenslauf.manager;

import org.lebenslauf.model.User;
import java.sql.*;
import java.util.Optional;

public class UserManager {
    private final DBConnection db;

    public UserManager(DBConnection db) {
        this.db = db;
    }

    public void addUser(User user) {
        String query = "INSERT INTO users (Full_Name, Email, Password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT id FROM users WHERE Email = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<User> getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE Email = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateUser(User user) {
        String query = "UPDATE users SET Full_Name = ?, Email = ?, Password = ? WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("Full_Name"),
                        rs.getString("Email"),
                        rs.getString("Password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
