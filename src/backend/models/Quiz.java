package backend.models;

import java.util.Date;

public class Quiz {
    private int quizId;
    private int courseId;
    private String title;
    private String quizType;
    private Date createdAt;

    public Quiz() {}

    public Quiz(int quizId, int courseId, String title, String quizType, Date createdAt) {
        this.quizId = quizId;
        this.courseId = courseId;
        this.title = title;
        this.quizType = quizType;
        this.createdAt = createdAt;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
