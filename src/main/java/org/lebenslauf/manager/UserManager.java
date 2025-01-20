package org.lebenslauf.manager;

import org.lebenslauf.model.User;
import org.lebenslauf.util.LogUtils;

import java.sql.*;
import java.util.Optional;

/**
 * Manager class for the User entity.
 */
public class UserManager extends BaseManager {
    public UserManager(DBConnection db) {
        super(db);
    }

    @Override
    public void logManagerInfo() {
        LogUtils.logInfo("UserManager initialized");
    }

    /**
     * Add a new user to the database.
     *
     * @param user the user object
     */
    public void addUser(User user) {
        String query = "INSERT INTO users (Full_Name, Email, Password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            String errMsg = "Error adding user to db: " + user.getEmail();
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
    }

    /**
     * Check if an email already exists in the database.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String query = "SELECT id FROM users WHERE Email = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            String errMsg = "Error checking if email exists: " + email;
            LogUtils.logError(e, errMsg);
            return false;
        }
    }

    /**
     * Get a user by email.
     *
     * @param email the email
     * @return an optional user object
     */
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
            String errMsg = "Error getting user by email: " + email;
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
        return Optional.empty();
    }

    /**
     * Update a user in the database.
     *
     * @param user the user object
     */
    public void updateUser(User user) {
        String query = "UPDATE users SET Full_Name = ?, Email = ?, Password = ? WHERE id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            String errMsg = "Error updating user: " + user.getEmail();
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
    }

    /**
     * Get a user by ID.
     *
     * @param id the user ID
     * @return an optional user object
     */
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
            String errMsg = "Error getting user by id: " + id;
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
        return Optional.empty();
    }
}
