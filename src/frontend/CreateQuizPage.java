package frontend;

import backend.controllers.CourseController;
import backend.controllers.QuizController;
import backend.models.Course;
import backend.models.Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

public class CreateQuizPage extends JDialog {

    private JComboBox<Course> courseCombo;
    private JTextField titleField;
    private JComboBox<String> typeCombo;
    private JButton createBtn;
    private JButton addQuestionsBtn;

    private CourseController courseController = new CourseController();
    private QuizController quizController = new QuizController();

    private Quiz createdQuiz;

    public CreateQuizPage(JFrame parent) {
        super(parent, "Create Quiz", true);
        setSize(520, 300);
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

        JLabel title = new JLabel("Create Quiz");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(courseLabel, gbc);
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(320, 28));
        gbc.gridx = 1;
        card.add(courseCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel titleLabel = new JLabel("Quiz Title:");
        titleLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(titleLabel, gbc);
        titleField = new JTextField();
        titleField.setPreferredSize(new Dimension(320, 28));
        gbc.gridx = 1;
        card.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel typeLabel = new JLabel("Quiz Type:");
        typeLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(typeLabel, gbc);
        typeCombo = new JComboBox<>(new String[]{"MCQ", "True/False", "Short Answer"});
        typeCombo.setPreferredSize(new Dimension(320, 28));
        gbc.gridx = 1;
        card.add(typeCombo, gbc);

        createBtn = new JButton("Create Quiz");
        UIUtils.applyPrimaryButton(createBtn);
        createBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createQuiz();
            }
        });

        addQuestionsBtn = new JButton("Add Questions");
        UIUtils.applySecondaryButton(addQuestionsBtn);
        addQuestionsBtn.setEnabled(false);
        addQuestionsBtn.addActionListener(e -> {
            if (createdQuiz != null) {
                // pass autoCreated = false when user explicitly clicks Add Questions
                new AddQuestionPage(this, createdQuiz.getQuizId(), false).setVisible(true);
            }
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12,0));
        btns.setOpaque(false);
        btns.add(createBtn);
        btns.add(addQuestionsBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        card.add(btns, gbc);

        c.gridx = 0; c.gridy = 0;
        root.add(card, c);

        add(root);
    }

    private void loadCourses() {
        try {
            // Load all courses and dedupe using UIUtils helper
            List<Course> courses = courseController.getAllCourses();
            DefaultComboBoxModel<Course> model = new DefaultComboBoxModel<>();
            java.util.List<Course> deduped = UIUtils.dedupeCoursesByName(courses);
            if (deduped != null) {
                for (Course c : deduped) model.addElement(c);
            }
            // If still empty, try creating defaults (existing logic preserved)
            if (model.getSize() == 0) {
                int teacherId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
                String[] defaults = new String[]{"OOP", "Programming Fundamentals", "Data Structures"};
                for (String name : defaults) {
                    try {
                        courseController.createCourse(name, teacherId);
                    } catch (Exception ignore) {}
                }
                // refresh
                List<Course> refreshed = courseController.getAllCourses();
                deduped = UIUtils.dedupeCoursesByName(refreshed);
                if (deduped != null) for (Course c : deduped) model.addElement(c);
            }
            if (model.getSize() == 0) {
                model.addElement(new Course(0, "(no courses)", 0));
                createBtn.setEnabled(false);
            } else {
                createBtn.setEnabled(true);
            }

            courseCombo.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createQuiz() {
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid course", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selected.getCourseId() <= 0) {
            // Try to persist placeholder course into DB so it has a valid ID
            int teacherId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
            boolean created = false;
            try {
                created = courseController.createCourse(selected.getCourseName(), teacherId);
            } catch (Exception ignore) {}

            if (created) {
                // refresh and pick the persisted course
                List<Course> refreshed = courseController.getAllCourses();
                if (refreshed != null) {
                    for (Course rc : refreshed) {
                        if (rc.getCourseName() != null && rc.getCourseName().equals(selected.getCourseName())) {
                            selected = rc;
                            break;
                        }
                    }
                }
            }

            if (selected.getCourseId() <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a valid course (unable to persist selected course)", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        String title = titleField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quiz title", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = quizController.createQuiz(selected.getCourseId(), title, type);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to create quiz", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // try to fetch the created quiz (best-effort) - pick quiz with highest quizId as the most recent
        try {
            List<Quiz> quizzes = quizController.getQuizzesByCourse(selected.getCourseId());
            if (quizzes != null && !quizzes.isEmpty()) {
                Quiz latest = null;
                for (Quiz q : quizzes) {
                    if (latest == null || q.getQuizId() > latest.getQuizId()) latest = q;
                }
                createdQuiz = latest;
            }
        } catch (Exception ignore) {}

        if (createdQuiz != null) {
            addQuestionsBtn.setEnabled(true);
            // show add question dialog immediately for convenience; since we just created the quiz, mark autoCreated = true
            new AddQuestionPage(this, createdQuiz.getQuizId(), true).setVisible(true);

            // after the modal dialog closes, verify the quiz still exists (it may have been canceled by the add-question dialog)
            Quiz persisted = quizController.getQuizById(createdQuiz.getQuizId());
            if (persisted != null) {
                createdQuiz = persisted;
                JOptionPane.showMessageDialog(this, "Quiz created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // quiz was removed (user returned without adding questions). reset state; quizCanceled already displayed info.
                createdQuiz = null;
                addQuestionsBtn.setEnabled(false);
            }
        } else {
            addQuestionsBtn.setEnabled(false);
        }
    }

    // Called by AddQuestionPage when the user returned without adding questions and the quiz was auto-created
    public void quizCanceled(int quizId) {
        if (createdQuiz != null && createdQuiz.getQuizId() == quizId) {
            boolean deleted = quizController.deleteQuiz(quizId);
            if (deleted) {
                createdQuiz = null;
                addQuestionsBtn.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Quiz was canceled and removed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete canceled quiz.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}