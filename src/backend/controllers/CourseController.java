package backend.controllers;

import backend.models.Course;
import backend.services.CourseService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CourseController {

    private CourseService courseService = new CourseService();

    // Shared static caches to reduce repeated DB calls across UI windows
    private static volatile List<Course> allCoursesCache;
    private static volatile long allCoursesCacheTime = 0;
    private static Map<Integer, CacheEntry<List<Course>>> teacherCoursesCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 5000; // 5s

    public boolean createCourse(String name, int teacherId) {
        boolean ok = courseService.createCourse(name, teacherId);
        if (ok) invalidateCaches();
        return ok;
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        if (teacherId <= 0) return courseService.getCoursesByTeacher(teacherId);
        CacheEntry<List<Course>> ent = teacherCoursesCache.get(teacherId);
        long now = System.currentTimeMillis();
        if (ent != null && now - ent.time <= CACHE_TTL_MS) return ent.value;
        List<Course> fresh = courseService.getCoursesByTeacher(teacherId);
        teacherCoursesCache.put(teacherId, new CacheEntry<>(fresh, now));
        return fresh;
    }

    public List<Course> getAllCourses() {
        long now = System.currentTimeMillis();
        if (allCoursesCache != null && now - allCoursesCacheTime <= CACHE_TTL_MS) return allCoursesCache;
        List<Course> fresh = courseService.getAllCourses();
        allCoursesCache = fresh;
        allCoursesCacheTime = now;
        return fresh;
    }

    public Course getCourseById(int id) {
        return courseService.getCourseById(id);
    }

    public boolean deleteCourse(int id) {
        boolean ok = courseService.deleteCourse(id);
        if (ok) invalidateCaches();
        return ok;
    }

    private void invalidateCaches() {
        allCoursesCache = null;
        allCoursesCacheTime = 0;
        teacherCoursesCache.clear();
    }

    private static class CacheEntry<T> {
        final T value;
        final long time;
        CacheEntry(T v, long t) { value = v; time = t; }
    }
}