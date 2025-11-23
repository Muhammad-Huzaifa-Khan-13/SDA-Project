package frontend;

import backend.controllers.QuestionController;
import backend.controllers.QuizController;
import backend.controllers.CourseController;
import backend.models.Question;
import backend.models.Quiz;
import backend.models.Course;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ManageQuestionsPage extends JDialog {

    private QuizController quizController = new QuizController();
    private QuestionController questionController = new QuestionController();
    private CourseController courseController = new CourseController();

    // Cache courseId -> courseName to avoid repeated DB calls during rendering
    private Map<Integer, String> courseNameCache = new HashMap<>();

    private JComboBox<Quiz> quizCombo;
    private DefaultListModel<Question> listModel;
    private JList<Question> questionList;

    // Keep references to controls so we can enable/disable during background loads
    private JButton loadBtn;
    private JButton addBtn;
    private JButton deleteBtn;

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
                    String courseName = courseNameCache.getOrDefault(q.getCourseId(), "(unknown course)");
                    setText(title + " (" + courseName + ")");
                } else {
                    setText(value != null ? value.toString() : "");
                }
                return this;
            }
        });
        top.add(quizCombo);
        loadBtn = new JButton("Load Questions");
        loadBtn.addActionListener(e -> loadQuestionsForSelectedQuiz());
        top.add(loadBtn);

        root.add(top, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        questionList = new JList<>(listModel);
        questionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        questionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Question) {
                    Question q = (Question) value;
                    // Show only the question text (no numeric id prefix)
                    setText(q.getQuestionText());
                }
                return this;
            }
        });

        root.add(new JScrollPane(questionList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        addBtn = new JButton("Add Question");
        addBtn.addActionListener(e -> openAddQuestion());
        deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelectedQuestions());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        bottom.add(addBtn);
        bottom.add(deleteBtn);
        bottom.add(closeBtn);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    private void loadQuizzes() {
        // Run loading in background to avoid blocking the UI thread.
        listModel.clear();
        quizCombo.removeAllItems();
        setLoadingState(true);

        SwingWorker<List<Quiz>, Void> worker = new SwingWorker<List<Quiz>, Void>() {
            private Exception error;

            @Override
            protected List<Quiz> doInBackground() {
                try {
                    try {
                        List<Course> courses = courseController.getAllCourses();
                        courseNameCache.clear();
                        if (courses != null) {
                            java.util.List<Course> dedupedCourses = UIUtils.dedupeCoursesByName(courses);
                            for (Course c : dedupedCourses) {
                                courseNameCache.put(c.getCourseId(), c.getCourseName());
                            }
                        }
                    } catch (Exception ignore) {
                        // leave cache empty; renderer will show fallback
                    }

                    // return deduped quizzes by title
                    java.util.List<Quiz> all = quizController.getAllQuizzes();
                    return UIUtils.dedupeQuizzesByTitle(all);
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    List<Quiz> quizzes = get();
                    if (quizzes != null) {
                        for (Quiz q : quizzes) quizCombo.addItem(q);
                    }
                } catch (Exception e) {
                    String msg = (error != null) ? error.getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Failed to load quizzes: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setLoadingState(false);
                }
            }
        };
        worker.execute();
    }

    private void loadQuestionsForSelectedQuiz() {
        Quiz selected = (Quiz) quizCombo.getSelectedItem();
        listModel.clear();
        if (selected == null) return;
        setLoadingState(true);

        SwingWorker<List<Question>, Void> worker = new SwingWorker<List<Question>, Void>() {
            private Exception error;

            @Override
            protected List<Question> doInBackground() {
                try {
                    return questionController.getQuestionsByQuiz(selected.getQuizId());
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    List<Question> qs = get();
                    if (qs != null) {
                        for (Question q : qs) listModel.addElement(q);
                    }
                } catch (Exception e) {
                    String msg = (error != null) ? error.getMessage() : e.getMessage();
                    JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Failed to load questions: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setLoadingState(false);
                }
            }
        };
        worker.execute();
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

    private void deleteSelectedQuestions() {
        java.util.List<Question> selected = questionList.getSelectedValuesList();
        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select one or more questions to delete", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int res = JOptionPane.showConfirmDialog(this, "Delete selected questions?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) return;

        boolean anyFailed = false;
        for (Question q : selected) {
            try {
                boolean ok = questionController.deleteQuestion(q.getQuestionId());
                if (!ok) anyFailed = true;
            } catch (Exception ignore) { anyFailed = true; }
        }

        if (anyFailed) JOptionPane.showMessageDialog(this, "Some questions may not have been deleted.", "Partial Result", JOptionPane.WARNING_MESSAGE);
        else JOptionPane.showMessageDialog(this, "Selected questions deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadQuestionsForSelectedQuiz();
    }

    // Utility to toggle UI state while loading data
    private void setLoadingState(boolean loading) {
        // show wait cursor for the dialog
        Cursor cursor = loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
        setCursor(cursor);
        // disable buttons while loading
        if (loadBtn != null) loadBtn.setEnabled(!loading);
        if (addBtn != null) addBtn.setEnabled(!loading);
        if (deleteBtn != null) deleteBtn.setEnabled(!loading);
        // also disable combo to prevent user changing selection mid-load
        if (quizCombo != null) quizCombo.setEnabled(!loading);
    }
}