package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Quiz;
import backend.models.QuizAssignment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAssignmentDAO {

    /**
     * Insert one or more assignments. studentId may be a single id or comma-separated ids.
     */
    public boolean insert(QuizAssignment assignment) {
        String sql = "INSERT INTO quiz_assignments (quiz_id, student_id, course_id, due_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String sids = assignment.getStudentId();
            if (sids == null || sids.trim().isEmpty()) {
                // nothing to insert
                return false;
            }

            String[] parts = sids.split(",");
            for (String p : parts) {
                String trimmed = p.trim();
                if (trimmed.isEmpty()) continue;
                int studentId;
                try {
                    studentId = Integer.parseInt(trimmed);
                } catch (NumberFormatException nfe) {
                    // skip invalid id
                    continue;
                }
                stmt.setInt(1, Integer.parseInt(assignment.getQuizId()));
                stmt.setInt(2, studentId);
                stmt.setInt(3, assignment.getCourseId());
                if (assignment.getDueAt() != null) stmt.setTimestamp(4, new Timestamp(assignment.getDueAt().getTime()));
                else stmt.setTimestamp(4, null);
                stmt.addBatch();
            }

            int[] res = stmt.executeBatch();
            int inserted = 0;
            for (int r : res) if (r >= 0) inserted++;
            return inserted > 0;

        } catch (BatchUpdateException bue) {
            try {
                int[] counts = bue.getUpdateCounts();
                for (int c : counts) if (c >= 0) return true;
            } catch (Exception ignored) {}
            System.out.println("QuizAssignment Insert Batch Error: " + bue.getMessage());
        } catch (Exception e) {
            System.out.println("QuizAssignment Insert Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Assign quiz to a list of students (helper)
     */
    public boolean insertForStudents(int quizId, List<Integer> studentIds, int courseId, Timestamp dueAt) {
        if (studentIds == null || studentIds.isEmpty()) return false;
        String sql = "INSERT INTO quiz_assignments (quiz_id, student_id, course_id, due_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer sid : studentIds) {
                stmt.setInt(1, quizId);
                stmt.setInt(2, sid);
                stmt.setInt(3, courseId);
                stmt.setTimestamp(4, dueAt);
                stmt.addBatch();
            }
            int[] res = stmt.executeBatch();
            for (int r : res) if (r >= 0) return true;
        } catch (Exception e) {
            System.out.println("InsertForStudents Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get quizzes assigned to a student within a specific course
     */
    public List<Quiz> getAssignedQuizzesForStudentAndCourse(int studentId, int courseId) {
        String sql = "SELECT q.* FROM quiz q JOIN quiz_assignments qa ON q.quiz_id = qa.quiz_id WHERE qa.student_id = ? AND q.course_id = ?";
        List<Quiz> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Quiz q = new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("quiz_type"),
                        rs.getTimestamp("created_at")
                );
                list.add(q);
            }

        } catch (Exception e) {
            System.out.println("GetAssignedQuizzes Error: " + e.getMessage());
        }

        return list;
    }
}