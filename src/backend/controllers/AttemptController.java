package backend.controllers;

import backend.models.Attempt;
import backend.services.AttemptService;

import java.util.List;

public class AttemptController {

    private AttemptService attemptService = new AttemptService();

    public Attempt startAttempt(int studentId, int quizId) {
        return attemptService.startAttempt(studentId, quizId);
    }

    public List<Attempt> getAttemptsByStudent(int studentId) {
        return attemptService.getAttemptsByStudent(studentId);
    }

    public List<Attempt> getAttemptsByQuiz(int quizId) {
        return attemptService.getAttemptsByQuiz(quizId);
    }

    public Attempt getAttemptById(int attemptId) {
        return attemptService.getAttemptById(attemptId);
    }
}
