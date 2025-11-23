package backend.controllers;

import backend.models.Quiz;
import backend.services.QuizService;

import java.util.List;

public class QuizController {

    private QuizService quizService = new QuizService();

    public boolean createQuiz(int courseId, String title, String quizType) {
        return quizService.createQuiz(courseId, title, quizType);
    }

    public Quiz getQuizById(int quizId) {
        return quizService.getQuizById(quizId);
    }

    public List<Quiz> getQuizzesByCourse(int courseId) {
        return quizService.getQuizzesByCourse(courseId);
    }

    // Added delete method so UI can remove a quiz if required
    public boolean deleteQuiz(int quizId) {
        return quizService.deleteQuiz(quizId);
    }
}