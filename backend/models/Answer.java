package backend.models;

public class Answer {
    private String answerId;
    private String attemptId;
    private String questionId;
    private String response;
    private float score;

    public Answer() {}

    public String getAnswerId() { return answerId; }
    public void setAnswerId(String answerId) { this.answerId = answerId; }

    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String attemptId) { this.attemptId = attemptId; }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
}
