package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    public boolean insert(Grade g) {
        String sql = "INSERT INTO grades (attempt_id, score) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, g.getAttemptId());
            stmt.setFloat(2, g.getScore());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next())
                    g.setGradeId(rs.getInt(1));
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Grade Error: " + e.getMessage());
        }

        return false;
    }

    public Grade getByAttempt(int attemptId) {
        String sql = "SELECT * FROM grades WHERE attempt_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractGrade(rs);
            }

        } catch (Exception e) {
            System.out.println("GetByAttempt Error: " + e.getMessage());
        }

        return null;
    }

    public List<Grade> getAll() {
        String sql = "SELECT * FROM grades";
        List<Grade> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractGrade(rs));
            }

        } catch (Exception e) {
            System.out.println("GetAll Error: " + e.getMessage());
        }

        return list;
    }

    private Grade extractGrade(ResultSet rs) throws SQLException {
        return new Grade(
            rs.getInt("grade_id"),
            rs.getInt("attempt_id"),
            rs.getFloat("score")
        );
    }
}
