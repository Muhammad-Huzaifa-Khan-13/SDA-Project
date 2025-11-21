package models;

public class Teacher extends User {
    private String department;

    public Teacher() {}

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
