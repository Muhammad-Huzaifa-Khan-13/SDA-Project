package backend.models;

import java.util.Date;

public class Quiz {
    private String quizId;
    private String title;
    private Date dueAt;
    private String status;

    public Quiz() {}

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getDueAt() { return dueAt; }
    public void setDueAt(Date dueAt) { this.dueAt = dueAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
