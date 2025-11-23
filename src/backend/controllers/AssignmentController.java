package backend.controllers;

import backend.dao.EnrollmentDAO;
import backend.dao.QuizAssignmentDAO;
import backend.models.Enrollment;
import backend.models.Quiz;
import backend.models.QuizAssignment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssignmentController {

    private QuizAssignmentDAO qaDao = new QuizAssignmentDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public AssignmentController() {}

    /**
     * Assign a quiz. If assignment.studentId contains comma-separated ids, those will be used.
     * If studentId is empty or equals "ALL", the quiz will be assigned to all students enrolled in assignment.courseId.
     */
    public void assignQuiz(QuizAssignment assignment) {
        if (assignment == null) return;

        String sids = assignment.getStudentId();
        int courseId = assignment.getCourseId();

        // assign to all students in course
        if (sids == null || sids.trim().isEmpty() || "ALL".equalsIgnoreCase(sids.trim())) {
            if (courseId <= 0) throw new IllegalArgumentException("Course ID required to assign to all students");
            List<Enrollment> enrolls = enrollmentDAO.getStudentsByCourse(courseId);
            List<Integer> studentIds = new ArrayList<>();
            if (enrolls != null) {
                for (Enrollment e : enrolls) studentIds.add(e.getStudentId());
            }
            Timestamp due = (assignment.getDueAt() != null) ? new Timestamp(assignment.getDueAt().getTime()) : null;
            boolean ok = qaDao.insertForStudents(Integer.parseInt(assignment.getQuizId()), studentIds, courseId, due);
            if (!ok) throw new RuntimeException("Failed to assign quiz to students");
            return;
        }

        // otherwise assign to listed student ids
        boolean ok = qaDao.insert(assignment);
        if (!ok) throw new RuntimeException("Failed to insert assignment");
    }

    public void updateAssignment(String assignmentId, QuizAssignment assignment) {}

    public void removeAssignment(String assignmentId) {}

    public List<Quiz> getAssignedQuizzesForStudentAndCourse(int studentId, int courseId) {
        return qaDao.getAssignedQuizzesForStudentAndCourse(studentId, courseId);
    }
}