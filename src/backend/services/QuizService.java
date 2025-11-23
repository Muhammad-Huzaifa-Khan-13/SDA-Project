package backend.services;

import backend.dao.QuizDAO;
import backend.dao.QuestionDAO;
import backend.models.Quiz;

import java.util.List;

public class QuizService {

    private QuizDAO quizDAO = new QuizDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private backend.dao.AnswerDAO answerDAO = new backend.dao.AnswerDAO();
    private backend.dao.GradeDAO gradeDAO = new backend.dao.GradeDAO();
    private backend.dao.AttemptDAO attemptDAO = new backend.dao.AttemptDAO();

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
        // delete associated answers for questions of this quiz
        answerDAO.deleteByQuiz(quizId);
        // delete grades for attempts of this quiz
        gradeDAO.deleteByQuiz(quizId);
        // delete attempts for this quiz
        attemptDAO.deleteByQuiz(quizId);
        // delete associated questions first to keep DB consistent
        questionDAO.deleteByQuiz(quizId);
        // delete reports for this quiz to avoid stale entries appearing in results
        try {
            backend.dao.ReportDAO reportDAO = new backend.dao.ReportDAO();
            reportDAO.deleteByQuiz(quizId);
        } catch (Exception ignore) {}
        return quizDAO.delete(quizId);
    }
}