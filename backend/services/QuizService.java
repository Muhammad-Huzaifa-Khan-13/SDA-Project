package backend.services;

import backend.dao.QuizDAO;
import backend.models.Quiz;

import java.util.List;

public class QuizService {

    private QuizDAO quizDAO = new QuizDAO();

    public boolean createQuiz(int courseId, String title, String quizType) {
        Quiz quiz = new Quiz(0, courseId, title, quizType, null);
        return quizDAO.insert(quiz);
    }

    public Quiz getQuizById(int quizId) {
        return quizDAO.getById(quizId);
    }

    public List<Quiz> getQuizzesByCourse(int courseId) {
        return quizDAO.getByCourse(courseId);
    }

    public List<Quiz> getAllQuizzes() {
        return quizDAO.getAll();
    }

    public boolean updateQuiz(Quiz quiz) {
        return quizDAO.update(quiz);
    }

    public boolean deleteQuiz(int quizId) {
        return quizDAO.delete(quizId);
    }
}
