package backend.services;

import backend.dao.UserDAO;
import backend.enums.Role;
import backend.models.User;
import backend.utils.PasswordUtils;

public class AuthenticationService {

    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {

        User user = userDAO.getByEmail(email);   // FIXED

        if (user == null) {
            System.out.println("User not found.");
            return null;
        }

        String stored = user.getPassword();
        // if stored is hashed, verify using PBKDF2
        if (PasswordUtils.isHashed(stored)) {
            if (!PasswordUtils.verifyPassword(password, stored)) {
                System.out.println("Incorrect password.");
                return null;
            }
            return user;
        }

        // fallback: legacy plain-text password in DB
        if (stored.equals(password)) {
            // migrate to hashed password
            userDAO.changePassword(user.getUserId(), password);
            return user;
        }

        System.out.println("Incorrect password.");
        return null;
    }

    public boolean register(String name, String email, String password, Role role) {

        User existing = userDAO.getByEmail(email);   // FIXED

        if (existing != null)
            return false; // prevent duplicate email

        User newUser = new User(0, name, email, password, role);

        return userDAO.insert(newUser);   // FIXED
    }
}