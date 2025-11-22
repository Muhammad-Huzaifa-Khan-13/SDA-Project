package backend.controllers;

import backend.models.Question;
import backend.services.QuestionService;

import java.util.List;

public class QuestionController {

    private QuestionService questionService = new QuestionService();

    public boolean addQuestion(Question q) {
        return questionService.addQuestion(q);
    }

    public List<Question> getQuestionsByQuiz(int quizId) {
        return questionService.getQuestionsByQuiz(quizId);
    }

    public boolean deleteQuestion(int id) {
        return questionService.deleteQuestion(id);
    }
}
