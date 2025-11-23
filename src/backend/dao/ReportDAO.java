package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public boolean insert(Report r) {
        String sql = "INSERT INTO reports (student_id, quiz_id, total_questions, correct_answers, percentage) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, r.getStudentId());
            stmt.setInt(2, r.getQuizId());
            stmt.setInt(3, r.getTotalQuestions());
            stmt.setInt(4, r.getCorrectAnswers());
            stmt.setFloat(5, r.getPercentage());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) r.setReportId(rs.getInt(1));
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Report Error: " + e.getMessage());
        }

        return false;
    }

    public Report getByStudentAndQuiz(int studentId, int quizId) {
        String sql = "SELECT * FROM reports WHERE student_id = ? AND quiz_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, quizId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extract(rs);
            }

        } catch (Exception e) {
            System.out.println("Get Report Error: " + e.getMessage());
        }

        return null;
    }

    public List<Report> getByStudent(int studentId) {
        String sql = "SELECT * FROM reports WHERE student_id = ?";
        List<Report> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) list.add(extract(rs));

        } catch (Exception e) {
            System.out.println("Get Reports Error: " + e.getMessage());
        }

        return list;
    }

    private Report extract(ResultSet rs) throws SQLException {
        return new Report(
                rs.getInt("report_id"),
                rs.getInt("student_id"),
                rs.getInt("quiz_id"),
                rs.getInt("total_questions"),
                rs.getInt("correct_answers"),
                rs.getFloat("percentage")
        );
    }
}
