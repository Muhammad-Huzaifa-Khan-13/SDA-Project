package frontend;

import backend.controllers.AssignmentController;
import backend.controllers.CourseController;
import backend.controllers.QuizController;
import backend.controllers.UserController;
import backend.models.Course;
import backend.models.Quiz;
import backend.models.QuizAssignment;
import backend.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AssignQuizPage extends JDialog {

    private JComboBox<Course> courseCombo;
    private JComboBox<Quiz> quizCombo;
    private JList<User> studentList;
    private DefaultListModel<User> studentListModel;
    private JCheckBox assignAllCheck;
    private JButton assignBtn;

    private CourseController courseController = new CourseController();
    private QuizController quizController = new QuizController();
    private AssignmentController assignmentController = new AssignmentController();
    private UserController userController = new UserController();

    public AssignQuizPage(JFrame parent) {
        super(parent, "Assign Quiz", true);
        setSize(740, 520);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadCourses();
    }

    private void initUI() {
        // Use BorderLayout so controls don't get clipped
        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBackground(UIUtils.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Assign Quiz to Students");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        card.add(new JLabel("Course:"), gbc);
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(360, 28));
        gbc.gridx = 1; card.add(courseCombo, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        card.add(new JLabel("Quiz:"), gbc);
        quizCombo = new JComboBox<>();
        // show quiz title in combobox
        quizCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof backend.models.Quiz) {
                    backend.models.Quiz q = (backend.models.Quiz) value;
                    setText(q.getTitle() != null ? q.getTitle() : ("Quiz #" + q.getQuizId()));
                }
                return this;
            }
        });
        quizCombo.setPreferredSize(new Dimension(360, 28));
        gbc.gridx = 1; card.add(quizCombo, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        card.add(new JLabel("Students (multi-select):"), gbc);
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        studentList.setVisibleRowCount(8);
        studentList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    User u = (User) value;
                    setText(u.getName() + " (" + u.getEmail() + ")");
                }
                return this;
            }
        });
        JScrollPane studentScroll = new JScrollPane(studentList);
        studentScroll.setPreferredSize(new Dimension(360, 160));
        gbc.gridx = 1; card.add(studentScroll, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        assignAllCheck = new JCheckBox("Assign to ALL students in course");
        assignAllCheck.setOpaque(false);
        card.add(assignAllCheck, gbc);

        // Bottom action area
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        assignBtn = new JButton("Assign Quiz");
        UIUtils.applyPrimaryButton(assignBtn);
        assignBtn.addActionListener(e -> assignQuiz());
        JButton close = new JButton("Close");
        UIUtils.applySecondaryButton(close);
        close.addActionListener(ev -> dispose());
        bottom.add(close);
        bottom.add(assignBtn);

        root.add(card, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        add(root);

        courseCombo.addActionListener(ev -> {
            Course sel = (Course) courseCombo.getSelectedItem();
            loadQuizzesForCourse(sel != null ? sel.getCourseId() : 0);
        });

        // populate students
        loadStudents();
    }

    private void loadCourses() {
        try {
            int teacherId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
            List<Course> courses = null;
            if (teacherId > 0) {
                courses = courseController.getCoursesByTeacher(teacherId);
            }
            // Fallback: if teacher has no courses or teacherId invalid, show all courses
            if (courses == null || courses.isEmpty()) {
                courses = courseController.getAllCourses();
            }
            // dedupe courses by name to collapse duplicates like multiple 'OOP'
            java.util.List<Course> dedupedCourses = UIUtils.dedupeCoursesByName(courses);
            DefaultComboBoxModel<Course> model = new DefaultComboBoxModel<>();
            if (dedupedCourses != null) {
                for (Course c : dedupedCourses) model.addElement(c);
            }
            if (model.getSize() == 0) {
                // No courses found anywhere
                courseCombo.setModel(new DefaultComboBoxModel<>(new Course[]{new Course(0, "(No courses available)", 0)}));
                courseCombo.setEnabled(false);
                // populate quizzes with all quizzes as a fallback
                DefaultComboBoxModel<Quiz> qm = new DefaultComboBoxModel<>();
                java.util.List<Quiz> all = quizController.getAllQuizzes();
                if (all != null) for (Quiz q : all) qm.addElement(q);
                quizCombo.setModel(qm);
                if (qm.getSize() == 0) {
                    quizCombo.setModel(new DefaultComboBoxModel<>(new Quiz[]{new Quiz(0, 0, "(No quizzes available)", "", null)}));
                    quizCombo.setEnabled(false);
                    assignBtn.setEnabled(false);
                } else {
                    quizCombo.setEnabled(true);
                    assignBtn.setEnabled(true);
                }
            } else {
                courseCombo.setModel(model);
                courseCombo.setEnabled(true);
                courseCombo.setSelectedIndex(0);
                // ensure quizzes load for the first course
                SwingUtilities.invokeLater(() -> loadQuizzesForCourse(((Course)model.getElementAt(0)).getCourseId()));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadQuizzesForCourse(int courseId) {
        try {
            DefaultComboBoxModel<Quiz> model = new DefaultComboBoxModel<>();
            java.util.List<Quiz> quizzes = quizController.getQuizzesByCourse(courseId);
            // dedupe by title to avoid multiple entries with same title
            java.util.List<Quiz> deduped = UIUtils.dedupeQuizzesByTitle(quizzes);
            if (deduped != null) {
                for (Quiz q : deduped) model.addElement(q);
            }
            if (model.getSize() == 0) {
                // show placeholder and disable assign if no quizzes
                quizCombo.setModel(new DefaultComboBoxModel<>(new Quiz[]{new Quiz(0, 0, "(No quizzes for this course)", "", null)}));
                quizCombo.setEnabled(false);
                assignBtn.setEnabled(false);
            } else {
                quizCombo.setModel(model);
                quizCombo.setEnabled(true);
                assignBtn.setEnabled(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load quizzes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudents() {
        try {
            java.util.List<User> users = userController.getAllUsers();
            studentListModel.clear();
            if (users != null) {
                for (User u : users) {
                    if (u.getRole() == backend.enums.Role.STUDENT) studentListModel.addElement(u);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignQuiz() {
        Quiz selectedQuiz = (Quiz) quizCombo.getSelectedItem();
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        String studentIds = "";
        if (selectedQuiz == null) {
            JOptionPane.showMessageDialog(this, "Please select a quiz", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // create assignment object; quizId and studentId are strings in model
        QuizAssignment assignment = new QuizAssignment();
        assignment.setQuizId(String.valueOf(selectedQuiz.getQuizId()));
        assignment.setCourseId(selectedCourse.getCourseId());

        try {
            if (assignAllCheck.isSelected()) {
                assignment.setStudentId("ALL");
            } else {
                List<User> selected = studentList.getSelectedValuesList();
                if (selected == null || selected.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select one or more students or choose 'Assign to ALL'", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (User u : selected) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(u.getUserId());
                }
                assignment.setStudentId(sb.toString());
            }

            assignmentController.assignQuiz(assignment);
            JOptionPane.showMessageDialog(this, "Quiz assigned.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Assignment failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}