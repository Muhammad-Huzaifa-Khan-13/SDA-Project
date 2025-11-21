package backend.controllers;

import backend.models.Answer;
import backend.models.Attempt;

public class AttemptController {

    public AttemptController() {}

    public Attempt startAttempt(String quizId, String studentId) { return null; }

    public void submitAnswer(String attemptId, Answer answer) {}

    public void finalizeAttempt(String attemptId) {}
}
