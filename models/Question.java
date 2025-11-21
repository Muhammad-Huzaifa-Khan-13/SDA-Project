package models;

public class Question {
    private String questionId;
    private String quizId;
    private String text;
    private String type;
    private float marks;
    private int order;

    public Question() {}

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public float getMarks() { return marks; }
    public void setMarks(float marks) { this.marks = marks; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
}
