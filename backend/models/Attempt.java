package backend.models;

import java.util.Date;

public class Attempt {

    private int attemptId;
    private int quizId;
    private int studentId;
    private Date attemptDate;

    public Attempt() {}

    public Attempt(int attemptId, int quizId, int studentId, Date attemptDate) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.studentId = studentId;
        this.attemptDate = attemptDate;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Date getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(Date attemptDate) {
        this.attemptDate = attemptDate;
    }
}
