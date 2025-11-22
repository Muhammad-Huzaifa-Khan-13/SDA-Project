package backend.services;

import backend.dao.ReportDAO;
import backend.models.Report;

import java.util.List;

public class ReportingService {

    private ReportDAO reportDAO = new ReportDAO();

    public boolean generateReport(int studentId, int quizId, int totalQuestions, int correctAnswers) {

        float percentage = ((float) correctAnswers / totalQuestions) * 100f;

        Report report = new Report(
                0,
                studentId,
                quizId,
                totalQuestions,
                correctAnswers,
                percentage
        );

        return reportDAO.insert(report);
    }

    public Report getReport(int studentId, int quizId) {
        return reportDAO.getByStudentAndQuiz(studentId, quizId);
    }

    public List<Report> getReportsOfStudent(int studentId) {
        return reportDAO.getByStudent(studentId);
    }
}
