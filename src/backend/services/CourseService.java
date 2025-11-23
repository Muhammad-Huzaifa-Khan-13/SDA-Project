package backend.services;

import backend.dao.CourseDAO;
import backend.models.Course;

import java.util.List;

public class CourseService {

    private CourseDAO courseDAO = new CourseDAO();

    public boolean createCourse(String courseName, int teacherId) {
        Course c = new Course(0, courseName, teacherId);
        return courseDAO.insert(c);
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        return courseDAO.getByTeacher(teacherId);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAll();
    }
}
