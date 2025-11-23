package backend.controllers;

import backend.enums.Role;
import backend.models.User;
import backend.services.AuthenticationService;

public class AuthController {

    private AuthenticationService authService = new AuthenticationService();

    public User login(String email, String password) {
        return authService.login(email, password);
    }

    public boolean register(String name, String email, String password, Role role) {
        return authService.register(name, email, password, role);
    }
}
