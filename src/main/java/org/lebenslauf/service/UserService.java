package org.lebenslauf.service;

import org.lebenslauf.model.User;
import org.lebenslauf.manager.DBConnection;
import org.lebenslauf.manager.UserManager;

import java.util.Optional;

public class UserService {
    private final UserManager userManager;

    public UserService(DBConnection db) {
        this.userManager = new UserManager(db);
    }

    public void addUser(User user) {
        userManager.addUser(user);
    }

    public boolean emailExists(String email) {
        return userManager.emailExists(email);
    }

    // TODO: validate email (darko?)
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

    public Optional<User> getUserById(int id) {
        return userManager.getUserById(id);
    }

    public void updateUser(User user) {
        userManager.updateUser(user);
    }
}
