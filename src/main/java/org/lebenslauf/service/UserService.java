package org.lebenslauf.service;

import org.lebenslauf.model.User;
import org.lebenslauf.manager.DBConnection;
import org.lebenslauf.manager.UserManager;

import java.util.Optional;

/**
 * Service class for user operations.
 */
public class UserService {
    private final UserManager userManager;

    /**
     * Constructor.
     *
     * @param db the database connection
     */
    public UserService(DBConnection db) {
        this.userManager = new UserManager(db);
        this.userManager.logManagerInfo();
    }

    /**
     * Add a new user to the database.
     *
     * @param user the user object
     */
    public void addUser(User user) {
        userManager.addUser(user);
    }

    /**
     * Check if an email already exists in the database.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userManager.emailExists(email);
    }

    /**
     * Validate user credentials.
     *
     * @param email the user's email
     * @param hashedPassword the user's hashed password
     * @return the user object if the credentials are valid, empty otherwise
     */
    public Optional<User> validateCredentials(String email, String hashedPassword) {
        Optional<User> userOpt = userManager.getUserByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (u.getPassword().equals(hashedPassword)) {
                return userOpt;
            }
        }
        return Optional.empty();
    }

    /**
     * Get a user by email.
     *
     * @param email the user's email
     * @return the user object if found, empty otherwise
     */
    public Optional<User> getUserById(int id) {
        return userManager.getUserById(id);
    }

    /**
     * Update a user in the database.
     *
     * @param user the user object
     */
    public void updateUser(User user) {
        userManager.updateUser(user);
    }
}
