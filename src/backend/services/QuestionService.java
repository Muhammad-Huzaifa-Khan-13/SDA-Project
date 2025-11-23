package backend.services;

import backend.dao.QuestionDAO;
import backend.models.Question;

import java.util.List;

public class QuestionService {

    private QuestionDAO questionDAO = new QuestionDAO();

    public boolean addQuestion(Question q) {
        return questionDAO.insert(q);
    }

    public List<Question> getQuestionsByQuiz(int quizId) {
        return questionDAO.getByQuizId(quizId);
    }

    public boolean deleteQuestion(int id) {
        return questionDAO.delete(id);
    }
}
