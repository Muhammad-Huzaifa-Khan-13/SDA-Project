package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Answer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {

    public boolean insert(Answer ans) {
        String sql = "INSERT INTO answers (attempt_id, question_id, selected_option, is_correct) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ans.getAttemptId());
            stmt.setInt(2, ans.getQuestionId());
            stmt.setString(3, ans.getSelectedOption());
            stmt.setBoolean(4, ans.isCorrect());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next())
                    ans.setAnswerId(rs.getInt(1));
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Answer Error: " + e.getMessage());
        }

        return false;
    }

    public List<Answer> getByAttempt(int attemptId) {
        String sql = "SELECT * FROM answers WHERE attempt_id = ?";
        List<Answer> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractAnswer(rs));
            }

        } catch (Exception e) {
            System.out.println("GetByAttempt Error: " + e.getMessage());
        }

        return list;
    }

    private Answer extractAnswer(ResultSet rs) throws SQLException {
        return new Answer(
                rs.getInt("answer_id"),
                rs.getInt("attempt_id"),
                rs.getInt("question_id"),
                rs.getString("selected_option"),
                rs.getBoolean("is_correct")
        );
    }
}
