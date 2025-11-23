package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public boolean insert(Question q) {
        String sql = "INSERT INTO questions (quiz_id, question_text, option_A, option_B, option_C, option_D, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, q.getQuizId());
            stmt.setString(2, q.getQuestionText());
            stmt.setString(3, q.getOptionA());
            stmt.setString(4, q.getOptionB());
            stmt.setString(5, q.getOptionC());
            stmt.setString(6, q.getOptionD());
            stmt.setString(7, q.getCorrectOption());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) q.setQuestionId(rs.getInt(1));
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Question Error: " + e.getMessage());
        }
        return false;
    }

    public List<Question> getByQuizId(int quizId) {
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        List<Question> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractQuestion(rs));
            }

        } catch (Exception e) {
            System.out.println("GetByQuiz Error: " + e.getMessage());
        }

        return list;
    }

    public Question getById(int id) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return extractQuestion(rs);

        } catch (Exception e) {
            System.out.println("GetById Error: " + e.getMessage());
        }

        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Delete Question Error: " + e.getMessage());
        }
        return false;
    }

    private Question extractQuestion(ResultSet rs) throws SQLException {
        return new Question(
            rs.getInt("question_id"),
            rs.getInt("quiz_id"),
            rs.getString("question_text"),
            rs.getString("option_A"),
            rs.getString("option_B"),
            rs.getString("option_C"),
            rs.getString("option_D"),
            rs.getString("correct_option")
        );
    }
}
