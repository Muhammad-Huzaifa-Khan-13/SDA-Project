package backend.controllers;

import backend.dao.UserDAO;
import backend.enums.Role;
import backend.models.User;

import java.util.List;

public class UserController {

    private UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    /**
     * Delete a user by id. Only an admin (actor) can perform this action.
     */
    public boolean deleteUser(int id, User actor) {
        if (actor == null) return false;
        if (actor.getRole() != Role.ADMIN) return false;
        return userDAO.delete(id);
    }

    /**
     * Change password for targetUserId. Admin can change any password. A user can change their own password.
     */
    public boolean changePassword(int targetUserId, String newPassword, User actor) {
        if (actor == null) return false;
        if (actor.getRole() == Role.ADMIN || actor.getUserId() == targetUserId) {
            return userDAO.changePassword(targetUserId, newPassword);
        }
        return false;
    }
}