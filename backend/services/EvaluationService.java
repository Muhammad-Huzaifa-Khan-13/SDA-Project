package backend.services;

import backend.dao.AnswerDAO;
import backend.dao.AttemptDAO;
import backend.dao.GradeDAO;
import backend.dao.QuestionDAO;
import backend.models.Answer;
import backend.models.Attempt;
import backend.models.Question;
import backend.models.Grade;

import java.util.List;

public class EvaluationService {

    private AttemptDAO attemptDAO = new AttemptDAO();
    private AnswerDAO answerDAO = new AnswerDAO();
    private QuestionDAO questionDAO = new QuestionDAO();
    private GradeDAO gradeDAO = new GradeDAO();

    public Attempt startAttempt(int studentId, int quizId) {
        Attempt a = new Attempt();
        a.setStudentId(studentId);
        a.setQuizId(quizId);

        boolean success = attemptDAO.insert(a);
        return success ? a : null;
    }

    public boolean submitAnswer(int attemptId, int questionId, String selectedOption) {
        Answer ans = new Answer(0, attemptId, questionId, selectedOption, false);
        return answerDAO.insert(ans);
    }

    public Grade evaluateAttempt(int attemptId) {
        List<Answer> answers = answerDAO.getByAttempt(attemptId);
        float totalScore = 0;

        for (Answer ans : answers) {
            Question q = questionDAO.getById(ans.getQuestionId());
            boolean isCorrect = ans.getSelectedOption().equalsIgnoreCase(q.getCorrectOption());
            ans.setCorrect(isCorrect);

            answerDAO.updateCorrectness(ans.getAnswerId(), isCorrect);

            if (isCorrect) totalScore += 1.0f;
        }

        Grade g = new Grade(0, attemptId, totalScore);
        gradeDAO.insert(g);

        return g;  
    }

    public boolean saveGrade(int attemptId, float score) {
        Grade g = new Grade(0, attemptId, score);
        return gradeDAO.insert(g);
    }
    public Grade getGrade(int attemptId) {
        return gradeDAO.getByAttempt(attemptId);
    }

   
    public boolean finalizeAttempt(int attemptId) {

        Grade g = evaluateAttempt(attemptId);  

        if (g == null) return false;

        float score = g.getScore();            

        saveGrade(attemptId, score);           

        return attemptDAO.updateStatus(attemptId, "SUBMITTED");
    }

}
