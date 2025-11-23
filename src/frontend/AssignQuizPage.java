package frontend;

import backend.controllers.AssignmentController;
import backend.controllers.CourseController;
import backend.controllers.QuizController;
import backend.models.Course;
import backend.models.Quiz;
import backend.models.QuizAssignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AssignQuizPage extends JDialog {

    private JComboBox<Course> courseCombo;
    private JComboBox<Quiz> quizCombo;
    private JTextField studentIdsField;
    private JButton assignBtn;

    private CourseController courseController = new CourseController();
    private QuizController quizController = new QuizController();
    private AssignmentController assignmentController = new AssignmentController();

    public AssignQuizPage(JFrame parent) {
        super(parent, "Assign Quiz", true);
        setSize(560, 260);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadCourses();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UIUtils.BACKGROUND);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);

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

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        card.add(new JLabel("Course:"), gbc);
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(320, 28));
        gbc.gridx = 1; card.add(courseCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        card.add(new JLabel("Quiz:"), gbc);
        quizCombo = new JComboBox<>();
        quizCombo.setPreferredSize(new Dimension(320, 28));
        gbc.gridx = 1; card.add(quizCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        card.add(new JLabel("Student IDs (comma-separated):"), gbc);
        studentIdsField = new JTextField();
        studentIdsField.setPreferredSize(new Dimension(320, 28));
        gbc.gridx = 1; card.add(studentIdsField, gbc);

        assignBtn = new JButton("Assign Quiz");
        UIUtils.applyPrimaryButton(assignBtn);
        assignBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignQuiz();
            }
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btns.setOpaque(false);
        btns.add(assignBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(btns, gbc);

        c.gridx = 0; c.gridy = 0;
        root.add(card, c);
        add(root);

        courseCombo.addActionListener(ev -> {
            Course sel = (Course) courseCombo.getSelectedItem();
            loadQuizzesForCourse(sel != null ? sel.getCourseId() : 0);
        });
    }

    private void loadCourses() {
        try {
            int teacherId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
            List<Course> courses = courseController.getCoursesByTeacher(teacherId);
            DefaultComboBoxModel<Course> model = new DefaultComboBoxModel<>();
            if (courses != null) {
                for (Course c : courses) model.addElement(c);
            }
            courseCombo.setModel(model);
            if (model.getSize() > 0) {
                courseCombo.setSelectedIndex(0);
                loadQuizzesForCourse(((Course)model.getElementAt(0)).getCourseId());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadQuizzesForCourse(int courseId) {
        try {
            DefaultComboBoxModel<Quiz> model = new DefaultComboBoxModel<>();
            java.util.List<Quiz> quizzes = quizController.getQuizzesByCourse(courseId);
            if (quizzes != null) {
                for (Quiz q : quizzes) model.addElement(q);
            }
            quizCombo.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load quizzes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignQuiz() {
        Quiz selectedQuiz = (Quiz) quizCombo.getSelectedItem();
        String studentIds = studentIdsField.getText().trim();
        if (selectedQuiz == null) {
            JOptionPane.showMessageDialog(this, "Please select a quiz", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter at least one student id", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // create assignment object; quizId and studentId are strings in model
        QuizAssignment assignment = new QuizAssignment();
        assignment.setQuizId(String.valueOf(selectedQuiz.getQuizId()));
        assignment.setStudentId(studentIds);

        try {
            assignmentController.assignQuiz(assignment);
            JOptionPane.showMessageDialog(this, "Quiz assigned (operation sent to backend).", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Assignment failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}