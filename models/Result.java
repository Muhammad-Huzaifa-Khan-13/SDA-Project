package models;

import java.util.Date;

public class Result {
    private String resultId;
    private String studentId;
    private String quizId;
    private float totalScore;
    private Date createdAt;

    public Result() {}

    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public float getTotalScore() { return totalScore; }
    public void setTotalScore(float totalScore) { this.totalScore = totalScore; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
