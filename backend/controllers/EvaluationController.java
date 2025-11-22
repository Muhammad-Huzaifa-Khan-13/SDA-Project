package backend.controllers;

import backend.services.EvaluationService;
import backend.models.Grade;

public class EvaluationController {

    private EvaluationService evaluationService = new EvaluationService();

    public Grade evaluateAttempt(int attemptId) {
        return evaluationService.evaluateAttempt(attemptId);
    }

    public Grade getGradeForAttempt(int attemptId) {
        return evaluationService.getGrade(attemptId);
    }
}
