package backend.controllers;

import backend.models.Report;
import backend.services.ReportingService;

import java.util.List;

public class ReportController {

    private ReportingService reportingService = new ReportingService();

    public boolean generateReport(int studentId, int quizId, int totalQuestions, int correctAnswers) {
        return reportingService.generateReport(studentId, quizId, totalQuestions, correctAnswers);
    }

    public Report getReport(int studentId, int quizId) {
        return reportingService.getReport(studentId, quizId);
    }

    public List<Report> getReportsOfStudent(int studentId) {
        return reportingService.getReportsOfStudent(studentId);
    }
}
