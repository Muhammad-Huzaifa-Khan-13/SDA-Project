package frontend;

import backend.controllers.CourseController;
import backend.controllers.QuizController;
import backend.models.Course;
import backend.models.Quiz;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.SwingWorker;

public class ManageQuizPage extends JDialog {

    private CourseController courseController = new CourseController();
    private QuizController quizController = new QuizController();
    // cache courses to avoid N+1 DB calls from the renderer
    private Map<Integer, Course> courseCache = new HashMap<>();

    private JComboBox<Course> courseCombo;
    private JTextField titleField;
    private JComboBox<String> typeCombo;
    private DefaultListModel<Quiz> quizListModel;
    private JList<Quiz> quizList;

    public ManageQuizPage(JFrame parent) {
        super(parent, "Manage Quizzes", true);
        setSize(700, 480);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        // load data in background so UI stays responsive
        loadDataAsync();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Course:"), gbc);
        courseCombo = new JComboBox<>();
        gbc.gridx = 1; form.add(courseCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Title:"), gbc);
        titleField = new JTextField();
        gbc.gridx = 1; form.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Type:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"MCQ", "True/False", "Short Answer"});
        gbc.gridx = 1; form.add(typeCombo, gbc);

        JButton createBtn = new JButton("Create Quiz");
        createBtn.addActionListener(e -> createQuiz());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(createBtn, gbc);

        root.add(form, BorderLayout.NORTH);

        quizListModel = new DefaultListModel<>();
        quizList = new JList<>(quizListModel);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Quiz) {
                    Quiz q = (Quiz) value;
                    // Show title and course name (no numeric prefix)
                    String title = q.getTitle() != null ? q.getTitle() : "(no title)";
                    String courseName = "(unknown course)";
                    Course course = courseCache.get(q.getCourseId());
                    if (course != null && course.getCourseName() != null) courseName = course.getCourseName();
                    setText(title + " (" + courseName + ")");
                }
                return this;
            }
        });

        root.add(new JScrollPane(quizList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        JButton deleteBtn = new JButton("Delete Selected Quiz");
        deleteBtn.addActionListener(e -> deleteSelectedQuiz());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        bottom.add(deleteBtn);
        bottom.add(closeBtn);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    // load courses and quizzes in background to avoid blocking EDT
    private void loadDataAsync() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // disable inputs while loading
        setEnabledForAll(this, false);

        new SwingWorker<Void, Void>() {
            List<Course> courses;
            List<Quiz> quizzes;
            Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    courses = courseController.getAllCourses();
                    quizzes = quizController.getAllQuizzes();
                } catch (Exception ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (error != null) throw error;

                    // update course cache and combo
                    courseCache.clear();
                    courseCombo.removeAllItems();
                    if (courses != null) {
                        for (Course c : courses) {
                            courseCache.put(c.getCourseId(), c);
                            courseCombo.addItem(c);
                        }
                    }

                    // update quiz list
                    quizListModel.clear();
                    if (quizzes != null) {
                        for (Quiz q : quizzes) quizListModel.addElement(q);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ManageQuizPage.this, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    setEnabledForAll(ManageQuizPage.this, true);
                }
            }
        }.execute();
    }

    // utility to enable/disable entire dialog recursively (simple approach)
    private void setEnabledForAll(Container c, boolean enabled) {
        c.setEnabled(enabled);
        for (Component comp : c.getComponents()) {
            comp.setEnabled(enabled);
            if (comp instanceof Container) setEnabledForAll((Container) comp, enabled);
        }
    }

    private void createQuiz() {
        Course c = (Course) courseCombo.getSelectedItem();
        if (c == null) { JOptionPane.showMessageDialog(this, "Please select a course", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        String title = titleField.getText().trim();
        if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter a title", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        String type = (String) typeCombo.getSelectedItem();

        boolean ok = quizController.createQuiz(c.getCourseId(), title, type);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Quiz created.", "Success", JOptionPane.INFORMATION_MESSAGE);
            titleField.setText("");
            // refresh in background
            loadDataAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create quiz.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedQuiz() {
        Quiz sel = quizList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Please select a quiz to delete", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        int res = JOptionPane.showConfirmDialog(this, "Delete selected quiz and all its questions?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.YES_OPTION) return;

        // Delete associated questions first (so numbering resets when recreated)
        boolean ok = quizController.deleteQuiz(sel.getQuizId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Quiz deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // refresh in background
            loadDataAsync();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete quiz.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}