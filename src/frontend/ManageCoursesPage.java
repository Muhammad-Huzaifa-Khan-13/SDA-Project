package frontend;

import backend.controllers.CourseController;
import backend.models.Course;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ManageCoursesPage extends JDialog {

    private CourseController courseController = new CourseController();
    private DefaultListModel<Course> courseListModel;
    private JList<Course> courseList;
    private JTextField nameField;

    public ManageCoursesPage(JFrame parent) {
        super(parent, "Manage Courses", true);
        setSize(520, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadCourses();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        top.add(new JLabel("Course Name:"), gbc);
        nameField = new JTextField();
        gbc.gridx = 1; gbc.weightx = 1.0; top.add(nameField, gbc);

        JButton create = new JButton("Create Course");
        UIUtils.applyPrimaryButton(create);
        create.addActionListener(e -> createCourse());
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0;
        top.add(create, gbc);

        root.add(top, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCourseName() != null ? c.getCourseName() : "(no name)");
                }
                return this;
            }
        });

        root.add(new JScrollPane(courseList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton delete = new JButton("Delete Selected");
        UIUtils.applySecondaryButton(delete);
        delete.addActionListener(e -> deleteSelected());
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        bottom.add(delete);
        bottom.add(close);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    private void loadCourses() {
        courseListModel.clear();
        try {
            List<Course> courses = courseController.getAllCourses();
            java.util.List<Course> deduped = UIUtils.dedupeCoursesByName(courses);
            if (deduped != null) for (Course c : deduped) courseListModel.addElement(c);
            // fallback
            if (courseListModel.isEmpty() && courses != null) for (Course c : courses) courseListModel.addElement(c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCourse() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a course name", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        int teacherId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
        boolean ok = false;
        try { ok = courseController.createCourse(name, teacherId); } catch (Exception ignored) {}
        if (ok) {
            JOptionPane.showMessageDialog(this, "Course created.", "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            loadCourses();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        java.util.List<Course> selected = courseList.getSelectedValuesList();
        if (selected == null || selected.isEmpty()) { JOptionPane.showMessageDialog(this, "Select one or more courses to delete", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        int res = JOptionPane.showConfirmDialog(this, "Delete selected courses? This will remove associated quizzes/questions.", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) return;
        boolean anyFailed = false;
        for (Course c : selected) {
            try { if (!courseController.deleteCourse(c.getCourseId())) anyFailed = true; } catch (Exception ignored) { anyFailed = true; }
        }
        if (anyFailed) JOptionPane.showMessageDialog(this, "Some courses may not have been deleted.", "Partial", JOptionPane.WARNING_MESSAGE);
        else JOptionPane.showMessageDialog(this, "Selected courses deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadCourses();
    }
}
