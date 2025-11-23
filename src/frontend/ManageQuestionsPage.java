package frontend;

import backend.controllers.QuestionController;
import backend.controllers.QuizController;
import backend.models.Question;
import backend.models.Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageQuestionsPage extends JDialog {

    private QuizController quizController = new QuizController();
    private QuestionController questionController = new QuestionController();

    private JComboBox<Quiz> quizCombo;
    private DefaultListModel<Question> listModel;
    private JList<Question> questionList;

    public ManageQuestionsPage(JFrame parent) {
        super(parent, "Manage Questions", true);
        setSize(700, 480);
        setLocationRelativeTo(parent);
        setResizable(false);

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
        // Render combo entries as quiz title (course)
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
        JButton loadBtn = new JButton("Load Questions");
        loadBtn.addActionListener(e -> loadQuestionsForSelectedQuiz());
        top.add(loadBtn);

        root.add(top, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        questionList = new JList<>(listModel);
        questionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Question) {
                    Question q = (Question) value;
                    // Show question text without numeric prefix; include internal id in brackets for reference
                    setText("[" + q.getQuestionId() + "] " + q.getQuestionText());
                }
                return this;
            }
        });

        root.add(new JScrollPane(questionList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        JButton addBtn = new JButton("Add Question");
        addBtn.addActionListener(e -> openAddQuestion());
        JButton deleteBtn = new JButton("Delete Question");
        deleteBtn.addActionListener(e -> deleteSelectedQuestion());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        bottom.add(addBtn);
        bottom.add(deleteBtn);
        bottom.add(closeBtn);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    private void loadQuizzes() {
        listModel.clear();
        quizCombo.removeAllItems();
        try {
            List<Quiz> quizzes = quizController.getAllQuizzes();
            if (quizzes != null) {
                for (Quiz q : quizzes) quizCombo.addItem(q);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load quizzes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadQuestionsForSelectedQuiz() {
        Quiz selected = (Quiz) quizCombo.getSelectedItem();
        listModel.clear();
        if (selected == null) return;
        try {
            List<Question> qs = questionController.getQuestionsByQuiz(selected.getQuizId());
            if (qs != null) {
                for (Question q : qs) listModel.addElement(q);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddQuestion() {
        Quiz selected = (Quiz) quizCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a quiz first", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // open AddQuestionPage but autoCreated=false because quiz already exists
        AddQuestionPage dlg = new AddQuestionPage(this, selected.getQuizId(), false);
        dlg.setVisible(true);
        // reload questions after dialog closes
        loadQuestionsForSelectedQuiz();
    }

    private void deleteSelectedQuestion() {
        Question sel = questionList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int res = JOptionPane.showConfirmDialog(this, "Delete selected question?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) return;
        boolean ok = questionController.deleteQuestion(sel.getQuestionId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Question deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadQuestionsForSelectedQuiz();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete question.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}