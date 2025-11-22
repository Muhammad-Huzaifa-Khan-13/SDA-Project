package backend.models;

public class Grade {

    private int gradeId;
    private int attemptId;
    private float score;

    public Grade() {}

    public Grade(int gradeId, int attemptId, float score) {
        this.gradeId = gradeId;
        this.attemptId = attemptId;
        this.score = score;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
