package frontend;

import backend.controllers.CourseController;
import backend.controllers.QuizController;
import backend.models.Course;
import backend.models.Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
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
                new AddQuestionPage(this, createdQuiz.getQuizId()).setVisible(true);
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
            int teacherId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
            List<Course> courses = courseController.getCoursesByTeacher(teacherId);
            DefaultComboBoxModel<Course> model = new DefaultComboBoxModel<>();
            if (courses != null) {
                for (Course c : courses) model.addElement(c);
            }
            courseCombo.setModel(model);
            if (model.getSize() == 0) {
                courseCombo.addItem(new Course(0, "(no courses)", 0));
                createBtn.setEnabled(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createQuiz() {
        Course selected = (Course) courseCombo.getSelectedItem();
        if (selected == null || selected.getCourseId() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a valid course", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
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

        // try to fetch the created quiz (best-effort)
        try {
            List<Quiz> quizzes = quizController.getQuizzesByCourse(selected.getCourseId());
            if (quizzes != null && !quizzes.isEmpty()) {
                quizzes.sort(Comparator.comparing(Quiz::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
                createdQuiz = quizzes.get(quizzes.size()-1);
            }
        } catch (Exception ignore) {}

        addQuestionsBtn.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Quiz created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}