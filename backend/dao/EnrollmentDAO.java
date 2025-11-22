package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public boolean enrollStudent(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Enroll Error: " + e.getMessage());
        }

        return false;
    }

    public List<Enrollment> getCoursesByStudent(int studentId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        List<Enrollment> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extract(rs));
            }

        } catch (Exception e) {
            System.out.println("getCoursesByStudent Error: " + e.getMessage());
        }

        return list;
    }

    public List<Enrollment> getStudentsByCourse(int courseId) {
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        List<Enrollment> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extract(rs));
            }

        } catch (Exception e) {
            System.out.println("getStudentsByCourse Error: " + e.getMessage());
        }

        return list;
    }

    private Enrollment extract(ResultSet rs) throws SQLException {
        return new Enrollment(
            rs.getInt("enrollment_id"),
            rs.getInt("student_id"),
            rs.getInt("course_id")
        );
    }
}
