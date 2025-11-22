package backend.services;

import backend.dao.UserDAO;
import backend.enums.Role;
import backend.models.User;

public class AuthenticationService {

    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {

        User user = userDAO.getByEmail(email);   // FIXED

        if (user == null) {
            System.out.println("User not found.");
            return null;
        }

        if (!user.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            return null;
        }

        return user; // success
    }

    public boolean register(String name, String email, String password, Role role) {

        User existing = userDAO.getByEmail(email);   // FIXED

        if (existing != null)
            return false; // prevent duplicate email

        User newUser = new User(0, name, email, password, role);

        return userDAO.insert(newUser);   // FIXED
    }
}
