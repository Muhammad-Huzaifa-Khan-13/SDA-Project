package backend.models;

public class Grade {
    private String gradeId;
    private String attemptId;
    private float score;

    public Grade() {}

    public String getGradeId() { return gradeId; }
    public void setGradeId(String gradeId) { this.gradeId = gradeId; }

    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String attemptId) { this.attemptId = attemptId; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
}
