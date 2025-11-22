package backend.services;

import backend.dao.AttemptDAO;
import backend.models.Attempt;

import java.util.List;

public class AttemptService {

    private AttemptDAO attemptDAO = new AttemptDAO();

    public Attempt startAttempt(int studentId, int quizId) {
        Attempt a = new Attempt();
        a.setStudentId(studentId);
        a.setQuizId(quizId);

        boolean ok = attemptDAO.insert(a);
        return ok ? a : null;
    }

    public List<Attempt> getAttemptsByStudent(int studentId) {
        return attemptDAO.getByStudent(studentId);
    }

    public List<Attempt> getAttemptsByQuiz(int quizId) {
        return attemptDAO.getByQuiz(quizId);
    }

    public Attempt getAttemptById(int attemptId) {
        return attemptDAO.getById(attemptId);
    }
}
