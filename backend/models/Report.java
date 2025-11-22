package backend.models;

public class Report {

    private int reportId;
    private int studentId;
    private int quizId;
    private int totalQuestions;
    private int correctAnswers;
    private float percentage;

    public Report() {}

    public Report(int reportId, int studentId, int quizId,
                  int totalQuestions, int correctAnswers, float percentage) {

        this.reportId = reportId;
        this.studentId = studentId;
        this.quizId = quizId;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.percentage = percentage;
    }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public float getPercentage() { return percentage; }
    public void setPercentage(float percentage) { this.percentage = percentage; }
}
