package frontend;

import backend.controllers.AttemptController;
import backend.controllers.CourseController;
import backend.controllers.QuestionController;
import backend.controllers.QuizController;
import backend.controllers.ReportController;
import backend.models.Attempt;
import backend.models.Course;
import backend.models.Question;
import backend.models.Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AttemptQuizPage extends JDialog {

    private JComboBox<Course> courseCombo;
    private JComboBox<Quiz> quizCombo;
    private JButton startBtn;

    private JPanel questionPanelContainer;
    private CardLayout cardLayout;
    private JButton prevBtn, nextBtn, submitBtn;

    private CourseController courseController = new CourseController();
    private QuizController quizController = new QuizController();
    private AttemptController attemptController = new AttemptController();
    private QuestionController questionController = new QuestionController();
    private ReportController reportController = new ReportController();

    private List<Question> questions = new ArrayList<>();
    private List<String> answers; // store selected option per question (A/B/C/D)
    private int currentIndex = 0;
    private Attempt currentAttempt;

    public AttemptQuizPage(JFrame parent) {
        super(parent, "Attempt Quiz", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadCourses();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBackground(UIUtils.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // Top card for selecting course/quiz
        JPanel topCard = UIUtils.createCardPanel();
        topCard.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Start Quiz Attempt");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        topCard.add(title, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        topCard.add(new JLabel("Course:"), c);
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(420, 28));
        c.gridx = 1; topCard.add(courseCombo, c);

        c.gridx = 0; c.gridy = 2;
        topCard.add(new JLabel("Quiz:"), c);
        quizCombo = new JComboBox<>();
        quizCombo.setPreferredSize(new Dimension(420, 28));
        c.gridx = 1; topCard.add(quizCombo, c);

        startBtn = new JButton("Start Attempt");
        UIUtils.applyPrimaryButton(startBtn);
        startBtn.addActionListener(e -> startAttempt());

        JPanel bpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bpanel.setOpaque(false);
        bpanel.add(startBtn);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; topCard.add(bpanel, c);

        root.add(topCard, BorderLayout.NORTH);

        // Center: questions (CardLayout)
        questionPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        questionPanelContainer.setLayout(cardLayout);
        questionPanelContainer.setBackground(UIUtils.BACKGROUND);

        JScrollPane scroll = new JScrollPane(questionPanelContainer);
        scroll.setBorder(null);
        root.add(scroll, BorderLayout.CENTER);

        // Bottom navigation
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nav.setOpaque(false);
        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");
        submitBtn = new JButton("Submit");

        UIUtils.applySecondaryButton(prevBtn);
        UIUtils.applySecondaryButton(nextBtn);
        UIUtils.applyPrimaryButton(submitBtn);

        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        submitBtn.setEnabled(false);

        prevBtn.addActionListener(e -> showPrevious());
        nextBtn.addActionListener(e -> showNext());
        submitBtn.addActionListener(e -> submitAttempt());

        nav.add(prevBtn);
        nav.add(nextBtn);
        nav.add(submitBtn);

        root.add(nav, BorderLayout.SOUTH);

        add(root);

        courseCombo.addActionListener(ev -> {
            Course sel = (Course) courseCombo.getSelectedItem();
            loadQuizzesForCourse(sel != null ? sel.getCourseId() : 0);
        });
    }

    private void loadCourses() {
        try {
            java.util.List<Course> courses = courseController.getAllCourses();
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

    private void startAttempt() {
        Quiz selected = (Quiz) quizCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a quiz to start.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
        if (studentId == 0) {
            JOptionPane.showMessageDialog(this, "No student session found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            currentAttempt = attemptController.startAttempt(studentId, selected.getQuizId());
        } catch (Exception ex) {
            // attempt start failed but continue to try loading questions
            currentAttempt = null;
        }

        // load questions
        try {
            questions = questionController.getQuestionsByQuiz(selected.getQuizId());
            if (questions == null || questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available for this quiz.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            answers = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) answers.add("");

            buildQuestionCards();

            currentIndex = 0;
            cardLayout.show(questionPanelContainer, String.valueOf(currentIndex));

            prevBtn.setEnabled(false);
            nextBtn.setEnabled(questions.size() > 1);
            submitBtn.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load questions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildQuestionCards() {
        questionPanelContainer.removeAll();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            JPanel qp = new JPanel(new BorderLayout(8,8));
            qp.setBackground(UIUtils.BACKGROUND);
            qp.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

            JLabel qLabel = new JLabel("Q" + (i+1) + ": " + q.getQuestionText());
            qLabel.setFont(UIUtils.REGULAR_FONT);
            qp.add(qLabel, BorderLayout.NORTH);

            JPanel opts = new JPanel(new GridLayout(0,1,6,6));
            JRadioButton a = new JRadioButton("A. " + q.getOptionA());
            JRadioButton b = new JRadioButton("B. " + q.getOptionB());
            JRadioButton c = new JRadioButton("C. " + q.getOptionC());
            JRadioButton d = new JRadioButton("D. " + q.getOptionD());
            a.setActionCommand("A"); b.setActionCommand("B"); c.setActionCommand("C"); d.setActionCommand("D");

            ButtonGroup bg = new ButtonGroup();
            bg.add(a); bg.add(b); bg.add(c); bg.add(d);

            opts.add(a); opts.add(b); opts.add(c); opts.add(d);
            qp.add(opts, BorderLayout.CENTER);

            int idx = i;
            Action updateSelection = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String sel = e.getActionCommand();
                    answers.set(idx, sel);
                }
            };

            a.addActionListener(updateSelection);
            b.addActionListener(updateSelection);
            c.addActionListener(updateSelection);
            d.addActionListener(updateSelection);

            // if previously selected, set selection
            String prev = answers.get(i);
            if ("A".equals(prev)) a.setSelected(true);
            if ("B".equals(prev)) b.setSelected(true);
            if ("C".equals(prev)) c.setSelected(true);
            if ("D".equals(prev)) d.setSelected(true);

            questionPanelContainer.add(qp, String.valueOf(i));
        }

        questionPanelContainer.revalidate();
        questionPanelContainer.repaint();
    }

    private void showPrevious() {
        if (currentIndex <= 0) return;
        currentIndex--;
        cardLayout.show(questionPanelContainer, String.valueOf(currentIndex));
        nextBtn.setEnabled(true);
        prevBtn.setEnabled(currentIndex > 0);
    }

    private void showNext() {
        if (currentIndex >= questions.size()-1) return;
        currentIndex++;
        cardLayout.show(questionPanelContainer, String.valueOf(currentIndex));
        prevBtn.setEnabled(true);
        nextBtn.setEnabled(currentIndex < questions.size()-1);
    }

    private void submitAttempt() {
        if (questions == null || questions.isEmpty()) return;
        int total = questions.size();
        int correct = 0;
        for (int i = 0; i < total; i++) {
            String sel = answers.get(i);
            String corr = questions.get(i).getCorrectOption();
            if (sel != null && !sel.isEmpty() && corr != null && !corr.isEmpty()) {
                if (sel.equalsIgnoreCase(corr)) correct++;
            }
        }

        int studentId = Session.getInstance().getCurrentUser() != null ? Session.getInstance().getCurrentUser().getUserId() : 0;
        try {
            reportController.generateReport(studentId, questions.get(0).getQuizId(), total, correct);
        } catch (Exception ex) {
            // ignore backend failure but show result
        }

        JOptionPane.showMessageDialog(this, "You scored " + correct + " out of " + total, "Result", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}