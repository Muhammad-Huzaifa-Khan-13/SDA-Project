package models;

public class Student extends User {
    private String rollNo;
    private String section;

    public Student() {}

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
