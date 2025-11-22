package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Attempt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttemptDAO {

    public boolean insert(Attempt a) {
        String sql = "INSERT INTO attempts (quiz_id, student_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getQuizId());
            stmt.setInt(2, a.getStudentId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next())
                    a.setAttemptId(rs.getInt(1));
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Attempt Error: " + e.getMessage());
        }

        return false;
    }

    public Attempt getById(int id) {
        String sql = "SELECT * FROM attempts WHERE attempt_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAttempt(rs);
            }

        } catch (Exception e) {
            System.out.println("GetById Attempt Error: " + e.getMessage());
        }

        return null;
    }

    public List<Attempt> getByStudent(int studentId) {
        String sql = "SELECT * FROM attempts WHERE student_id = ?";
        List<Attempt> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractAttempt(rs));
            }

        } catch (Exception e) {
            System.out.println("GetByStudent Error: " + e.getMessage());
        }

        return list;
    }

    public List<Attempt> getByQuiz(int quizId) {
        String sql = "SELECT * FROM attempts WHERE quiz_id = ?";
        List<Attempt> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractAttempt(rs));
            }

        } catch (Exception e) {
            System.out.println("GetByQuiz Error: " + e.getMessage());
        }

        return list;
    }
    public boolean updateStatus(int attemptId, String status) {
        String sql = "UPDATE attempts SET status = ? WHERE attempt_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, attemptId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("UpdateStatus Error: " + e.getMessage());
        }
        return false;
    }


    private Attempt extractAttempt(ResultSet rs) throws SQLException {
        return new Attempt(
                rs.getInt("attempt_id"),
                rs.getInt("quiz_id"),
                rs.getInt("student_id"),
                rs.getTimestamp("attempt_date")
        );
    }
}
