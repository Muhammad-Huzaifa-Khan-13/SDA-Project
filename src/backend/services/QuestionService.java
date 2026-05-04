package backend.services;

import backend.dao.QuestionDAO;
import backend.dao.AnswerDAO;
import backend.models.Question;

import java.util.List;

public class QuestionService {

    private QuestionDAO questionDAO = new QuestionDAO();
    private AnswerDAO answerDAO = new AnswerDAO();

    public boolean addQuestion(Question q) {
        return questionDAO.insert(q);
    }

    public List<Question> getQuestionsByQuiz(int quizId) {
        return questionDAO.getByQuizId(quizId);
    }

    public boolean deleteQuestion(int id) {
        // delete answers associated with this question first
        answerDAO.deleteByQuestion(id);
        return questionDAO.delete(id);
    }
}