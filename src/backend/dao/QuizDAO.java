package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public boolean insert(Quiz quiz) {
        String sql = "INSERT INTO quiz (course_id, title, quiz_type) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, quiz.getCourseId());
            stmt.setString(2, quiz.getTitle());
            stmt.setString(3, quiz.getQuizType());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    quiz.setQuizId(rs.getInt(1));
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Quiz Error: " + e.getMessage());
        }
        return false;
    }

    public Quiz getById(int id) {
        String sql = "SELECT * FROM quiz WHERE quiz_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractQuiz(rs);
            }

        } catch (Exception e) {
            System.out.println("GetById Quiz Error: " + e.getMessage());
        }
        return null;
    }

    public List<Quiz> getByCourse(int courseId) {
        String sql = "SELECT * FROM quiz WHERE course_id = ?";
        List<Quiz> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractQuiz(rs));
            }

        } catch (Exception e) {
            System.out.println("GetByCourse Error: " + e.getMessage());
        }
        return list;
    }

    public List<Quiz> getAll() {
        String sql = "SELECT * FROM quiz";
        List<Quiz> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractQuiz(rs));
            }

        } catch (Exception e) {
            System.out.println("GetAll Quiz Error: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Quiz quiz) {
        String sql = "UPDATE quiz SET course_id = ?, title = ?, quiz_type = ? WHERE quiz_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quiz.getCourseId());
            stmt.setString(2, quiz.getTitle());
            stmt.setString(3, quiz.getQuizType());
            stmt.setInt(4, quiz.getQuizId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Update Quiz Error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM quiz WHERE quiz_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Delete Quiz Error: " + e.getMessage());
        }
        return false;
    }

    private Quiz extractQuiz(ResultSet rs) throws SQLException {
        return new Quiz(
                rs.getInt("quiz_id"),
                rs.getInt("course_id"),
                rs.getString("title"),
                rs.getString("quiz_type"),
                rs.getTimestamp("created_at")
        );
    }
}
