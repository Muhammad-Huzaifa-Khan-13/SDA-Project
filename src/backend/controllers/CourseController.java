package backend.controllers;

import backend.models.Course;
import backend.services.CourseService;

import java.util.List;

public class CourseController {

    private CourseService courseService = new CourseService();

    public boolean createCourse(String name, int teacherId) {
        return courseService.createCourse(name, teacherId);
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        return courseService.getCoursesByTeacher(teacherId);
    }

    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    public Course getCourseById(int id) {
        return courseService.getCourseById(id);
    }
}