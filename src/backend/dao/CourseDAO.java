package backend.dao;

import backend.db.DatabaseConnection;
import backend.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public boolean insert(Course c) {
        String sql = "INSERT INTO courses (course_name, teacher_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, c.getCourseName());
            stmt.setInt(2, c.getTeacherId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    c.setCourseId(rs.getInt(1));
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println("Insert Course Error: " + e.getMessage());
        }
        return false;
    }

    public Course getById(int id) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCourse(rs);
            }

        } catch (Exception e) {
            System.out.println("GetById Error: " + e.getMessage());
        }
        return null;
    }

    public List<Course> getByTeacher(int teacherId) {
        String sql = "SELECT * FROM courses WHERE teacher_id = ?";
        List<Course> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(extractCourse(rs));
            }

        } catch (Exception e) {
            System.out.println("GetByTeacher Error: " + e.getMessage());
        }
        return list;
    }

    public List<Course> getAll() {
        String sql = "SELECT * FROM courses";
        List<Course> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractCourse(rs));
            }

        } catch (Exception e) {
            System.out.println("GetAll Error: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Course c) {
        String sql = "UPDATE courses SET course_name = ?, teacher_id = ? WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getCourseName());
            stmt.setInt(2, c.getTeacherId());
            stmt.setInt(3, c.getCourseId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
        return false;
    }


    private Course extractCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getInt("teacher_id")
        );
    }
}
