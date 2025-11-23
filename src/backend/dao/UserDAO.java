package backend.dao;

import backend.db.DatabaseConnection;
import backend.enums.Role;
import backend.models.User;
import backend.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean insert(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            // ensure password is hashed before storing
            String pwd = user.getPassword();
            if (pwd == null) pwd = "";
            if (!PasswordUtils.isHashed(pwd)) pwd = PasswordUtils.hashPassword(pwd);
            stmt.setString(3, pwd);
            stmt.setString(4, user.getRole().name());

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert User Error: " + e.getMessage());
        }
        return false;
    }

    public User getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }

        } catch (Exception e) {
            System.out.println("GetByEmail Error: " + e.getMessage());
        }
        return null;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }

        } catch (Exception e) {
            System.out.println("GetById Error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }

        } catch (Exception e) {
            System.out.println("GetAll Error: " + e.getMessage());
        }
        return users;
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            // ensure password is hashed
            String pwd = user.getPassword();
            if (pwd == null) pwd = "";
            if (!PasswordUtils.isHashed(pwd)) pwd = PasswordUtils.hashPassword(pwd);
            stmt.setString(3, pwd);
            stmt.setString(4, user.getRole().name());
            stmt.setInt(5, user.getUserId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
        }
        return false;
    }

    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashed = newPassword;
            if (!PasswordUtils.isHashed(hashed)) hashed = PasswordUtils.hashPassword(newPassword);
            stmt.setString(1, hashed);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ChangePassword Error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
        return false;
    }

    // helper to build User object
    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role"))
        );
    }
}