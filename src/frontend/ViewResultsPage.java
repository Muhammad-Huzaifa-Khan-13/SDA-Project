package frontend;

import backend.controllers.AttemptController;
import backend.controllers.EvaluationController;
import backend.controllers.QuizController;
import backend.controllers.UserController;
import backend.models.Attempt;
import backend.models.Grade;
import backend.models.Quiz;
import backend.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ViewResultsPage extends JDialog {

    private QuizController quizController = new QuizController();
    private AttemptController attemptController = new AttemptController();
    private EvaluationController evaluationController = new EvaluationController();
    private UserController userController = new UserController();

    private JComboBox<Quiz> quizCombo;
    private DefaultTableModel tableModel;
    private JTable resultsTable;

    public ViewResultsPage(JFrame parent) {
        super(parent, "View Results", true);
        setSize(800, 520);
        setLocationRelativeTo(parent);
        setResizable(true);

        initUI();
        loadQuizzes();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Select Quiz:"));
        quizCombo = new JComboBox<>();
        quizCombo.setPreferredSize(new Dimension(420, 28));
        // show quiz title and course
        quizCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Quiz) {
                    Quiz q = (Quiz) value;
                    String title = q.getTitle() != null ? q.getTitle() : "(no title)";
                    String courseName = "(unknown course)";
                    try {
                        backend.controllers.CourseController cc = new backend.controllers.CourseController();
                        backend.models.Course course = cc.getCourseById(q.getCourseId());
                        if (course != null && course.getCourseName() != null) courseName = course.getCourseName();
                    } catch (Exception ignore) {}
                    setText(title + " (" + courseName + ")");
                } else {
                    setText(value != null ? value.toString() : "");
                }
                return this;
            }
        });
        top.add(quizCombo);

        JButton loadBtn = new JButton("Load Attempts");
        loadBtn.addActionListener(e -> loadAttemptsForSelectedQuiz());
        top.add(loadBtn);

        root.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Attempt ID", "Student", "Attempt Date", "Score"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        root.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(closeBtn);
        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    private void loadQuizzes() {
        quizCombo.removeAllItems();
        try {
            List<Quiz> quizzes = quizController.getAllQuizzes();
            if (quizzes != null) for (Quiz q : quizzes) quizCombo.addItem(q);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load quizzes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAttemptsForSelectedQuiz() {
        Quiz selected = (Quiz) quizCombo.getSelectedItem();
        tableModel.setRowCount(0);
        if (selected == null) return;

        try {
            List<Attempt> attempts = attemptController.getAttemptsByQuiz(selected.getQuizId());
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (attempts != null) {
                for (Attempt a : attempts) {
                    User student = userController.getUserById(a.getStudentId());
                    String studentName = student != null ? student.getName() : String.valueOf(a.getStudentId());

                    Grade g = evaluationController.getGradeForAttempt(a.getAttemptId());
                    String score = g != null ? String.valueOf(g.getScore()) : "(not graded)";

                    String attemptedAt = a.getAttemptDate() != null ? fmt.format(a.getAttemptDate()) : "";
                    tableModel.addRow(new Object[]{a.getAttemptId(), studentName, attemptedAt, score});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load attempts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}