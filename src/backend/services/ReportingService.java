package backend.services;

import backend.dao.ReportDAO;
import backend.dao.QuizDAO;
import backend.models.Report;

import java.util.List;

public class ReportingService {

    private ReportDAO reportDAO = new ReportDAO();
    private QuizDAO quizDAO = new QuizDAO();

    public boolean generateReport(int studentId, int quizId, int totalQuestions, int correctAnswers) {

        // don't generate reports for quizzes that do not exist
        try {
            if (quizDAO.getById(quizId) == null) return false;
        } catch (Exception e) {
            return false;
        }

        if (totalQuestions <= 0) return false;
        float percentage = ((float) correctAnswers / totalQuestions) * 100f;

        Report report = new Report(0, studentId, quizId, totalQuestions, correctAnswers, percentage);
        return reportDAO.insert(report);
    }

    public Report getReport(int studentId, int quizId) {
        return reportDAO.getByStudentAndQuiz(studentId, quizId);
    }

    public List<Report> getReportsOfStudent(int studentId) {
        return reportDAO.getByStudent(studentId);
    }
}