package models;

public class Report {
    private String reportId;
    private String classId;
    private String summary;

    public Report() {}

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
