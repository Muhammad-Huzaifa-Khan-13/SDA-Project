package frontend;

import backend.controllers.ReportController;
import backend.controllers.QuizController;
import backend.controllers.CourseController;
import backend.controllers.UserController;
import backend.models.Report;
import backend.models.User;
import backend.models.Quiz;
import backend.models.Course;
import backend.enums.Role;
import backend.controllers.AttemptController;
import backend.models.Attempt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ResultPage extends JDialog {

    private JComboBox<User> studentCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private ReportController reportController = new ReportController();
    private UserController userController = new UserController();
    private QuizController quizController = new QuizController();
    private CourseController courseController = new CourseController();
    private AttemptController attemptController = new AttemptController();
    // currently displayed student id (used when exporting selected report)
    private int displayedStudentId = 0;

    public ResultPage(JFrame parent) {
        super(parent, "Results", true);
        setSize(700, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadInitialData();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBackground(UIUtils.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);

        JLabel title = new JLabel("Reports");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        top.add(title);

        // If current user is admin/teacher allow selecting student
        User current = Session.getInstance().getCurrentUser();
        if (current != null && (current.getRole() == Role.ADMIN || current.getRole() == Role.TEACHER)) {
            top.add(new JLabel("  Select Student: "));
            studentCombo = new JComboBox<>();
            studentCombo.setPreferredSize(new Dimension(260, 28));
            studentCombo.addActionListener(e -> refreshTable());
            top.add(studentCombo);
        }

        root.add(top, BorderLayout.NORTH);

        // Table: do not show report IDs; include a hidden quizId column (last column) used for exports
        tableModel = new DefaultTableModel(new Object[]{"Quiz Title","Course","Total Q","Correct","%","_quizId"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        // hide the last column (quiz id) from view but keep it in the model for export/logic
        table.removeColumn(table.getColumnModel().getColumn(table.getColumnCount()-1));
        JScrollPane sp = new JScrollPane(table);
        root.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton download = new JButton("Download Selected");
        UIUtils.applyPrimaryButton(download);
        download.addActionListener(e -> onDownloadSelected());

        JButton downloadFull = new JButton("Download Full Quiz");
        UIUtils.applySecondaryButton(downloadFull);
        downloadFull.addActionListener(e -> onDownloadFullQuiz());

        JButton close = new JButton("Close");
        UIUtils.applySecondaryButton(close);
        close.addActionListener(e -> dispose());
        bottom.add(download);
        bottom.add(downloadFull);
        bottom.add(close);
        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    private void loadInitialData() {
        User current = Session.getInstance().getCurrentUser();
        if (current == null) return;

        if (current.getRole() == Role.STUDENT) {
            // show only this student's reports
            studentCombo = null;
            loadReportsForStudent(current.getUserId());
        } else {
            // populate studentCombo with students using UserController (cached) in background
            // studentCombo instance was created in initUI; populate its model here
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<java.util.List<User>, Void>() {
                Exception err;
                @Override
                protected java.util.List<User> doInBackground() {
                    try { return userController.getAllUsers(); } catch (Exception e) { err = e; return null; }
                }

                @Override
                protected void done() {
                    try {
                        if (err != null) throw err;
                        java.util.List<User> users = get();
                        DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
                        if (users != null) {
                            for (User u : users) if (u.getRole() == Role.STUDENT) model.addElement(u);
                        }
                        studentCombo.setModel(model);
                        if (model.getSize() > 0) studentCombo.setSelectedIndex(0);
                        refreshTable();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ResultPage.this, "Failed to load students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        }
    }

    private void refreshTable() {
        User sel = (studentCombo != null) ? (User) studentCombo.getSelectedItem() : Session.getInstance().getCurrentUser();
        if (sel == null) return;
        loadReportsForStudent(sel.getUserId());
    }

    private void loadReportsForStudent(int studentId) {
        tableModel.setRowCount(0);
        displayedStudentId = studentId;
        try {
            List<Report> reports = reportController.getReportsOfStudent(studentId);
            if (reports != null) {
                for (Report r : reports) {
                    int qid = r.getQuizId();
                    // Skip reports that reference quizzes that no longer exist
                    Quiz q = null;
                    try { q = quizController.getQuizById(qid); } catch (Exception ignore) { q = null; }
                    if (q == null) continue;

                    String title = q.getTitle() != null ? q.getTitle() : "(no title)";
                    String courseName = "(unknown course)";
                    try {
                        Course c = courseController.getCourseById(q.getCourseId());
                        if (c != null && c.getCourseName() != null) courseName = c.getCourseName();
                    } catch (Exception ignore) {}
                    tableModel.addRow(new Object[]{title, courseName, r.getTotalQuestions(), r.getCorrectAnswers(), r.getPercentage(), qid});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load reports: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Download the selected report (student or teacher)
    private void onDownloadSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a report to download.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // hidden quizId is stored in the model's last column
        int quizId = (int) tableModel.getValueAt(row, tableModel.getColumnCount()-1);
        // We know which student is currently displayed
        int studentId = displayedStudentId;

        // ensure the quiz still exists
        Quiz q = quizController.getQuizById(quizId);
        if (q == null) {
            JOptionPane.showMessageDialog(this, "Selected quiz has been deleted; report is not available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            // refresh table to remove any stale entries
            refreshTable();
            return;
        }

        Report r = reportController.getReport(studentId, quizId);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Selected report not available for download.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Report> list = new ArrayList<>();
        list.add(r);
        exportReports(list, "report_student_" + studentId + "_quiz_" + quizId);
    }

    // Download full quiz report - for teachers. Use selected row's quizId if available, else prompt.
    private void onDownloadFullQuiz() {
        User current = Session.getInstance().getCurrentUser();
        if (current == null || current.getRole() != Role.TEACHER) {
            JOptionPane.showMessageDialog(this, "Only teachers can download full quiz reports.", "Permission", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = table.getSelectedRow();
        Integer quizId = null;
        if (row >= 0) quizId = (int) tableModel.getValueAt(row, 6); // hidden column

        if (quizId == null) {
            String s = JOptionPane.showInputDialog(this, "Enter Quiz ID to export:", "Quiz ID", JOptionPane.PLAIN_MESSAGE);
            if (s == null) return;
            try { quizId = Integer.parseInt(s.trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid quiz id.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        }

        // ensure quiz exists
        Quiz q = quizController.getQuizById(quizId);
        if (q == null) {
            JOptionPane.showMessageDialog(this, "Quiz has been deleted or does not exist.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<Report> reports = getReportsForQuiz(quizId);
        if (reports == null || reports.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No reports found for quiz " + (q.getTitle() != null ? q.getTitle() : quizId), "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        exportReports(reports, "quiz_report_" + quizId);
    }

    // collect reports for given quiz by iterating attempts and fetching each student's report
    private List<Report> getReportsForQuiz(int quizId) {
        List<Report> out = new ArrayList<>();
        try {
            List<Attempt> attempts = attemptController.getAttemptsByQuiz(quizId);
            if (attempts != null) {
                for (Attempt a : attempts) {
                    Report r = reportController.getReport(a.getStudentId(), quizId);
                    if (r != null) out.add(r);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to collect reports: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return out;
    }

    // export to CSV or TXT using simple file IO
    private void exportReports(List<Report> reports, String suggestedName) {
        String[] opts = new String[]{"CSV","TXT"};
        int choice = JOptionPane.showOptionDialog(this, "Choose export format:", "Export", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        if (choice < 0) return;

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(suggestedName + (choice == 0 ? ".csv" : ".txt")));
        int res = fc.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();

        try (FileWriter fw = new FileWriter(file)) {
            int skipped = 0;
            if (choice == 0) {
                // CSV header with quiz title and course
                fw.write("report_id,student_id,quiz_title,course,total_questions,correct_answers,percentage\n");
                for (Report r : reports) {
                    int qid = r.getQuizId();
                    Quiz qq = quizController.getQuizById(qid);
                    if (qq == null) { skipped++; continue; }
                    String qt = qq.getTitle() != null ? qq.getTitle() : "(no title)";
                    String cn = "(unknown)";
                    try {
                        Course c = courseController.getCourseById(qq.getCourseId());
                        if (c != null && c.getCourseName() != null) cn = c.getCourseName();
                    } catch (Exception ignore) {}
                    fw.write(String.format("%d,%d,\"%s\",\"%s\",%d,%d,%.2f\n", r.getReportId(), r.getStudentId(), qt.replace("\"","\"\""), cn.replace("\"","\"\""), r.getTotalQuestions(), r.getCorrectAnswers(), r.getPercentage()));
                }
            } else {
                // TXT human readable with quiz title and course
                for (Report r : reports) {
                    int qid = r.getQuizId();
                    Quiz qq = quizController.getQuizById(qid);
                    if (qq == null) { skipped++; continue; }
                    String qt = qq.getTitle() != null ? qq.getTitle() : "(no title)";
                    String cn = "(unknown)";
                    try {
                        Course c = courseController.getCourseById(qq.getCourseId());
                        if (c != null && c.getCourseName() != null) cn = c.getCourseName();
                    } catch (Exception ignore) {}
                    fw.write("Report ID: " + r.getReportId() + "\n");
                    fw.write("Student ID: " + r.getStudentId() + "\n");
                    fw.write("Quiz: " + qt + "\n");
                    fw.write("Course: " + cn + "\n");
                    fw.write("Total Questions: " + r.getTotalQuestions() + "\n");
                    fw.write("Correct Answers: " + r.getCorrectAnswers() + "\n");
                    fw.write(String.format("Percentage: %.2f%%\n", r.getPercentage()));
                    fw.write("----------------------------------------\n");
                }
            }
            fw.flush();
            if (skipped > 0) JOptionPane.showMessageDialog(this, "Exported to " + file.getAbsolutePath() + " (" + skipped + " reports skipped because their quizzes were deleted)", "Partial Export", JOptionPane.WARNING_MESSAGE);
            else JOptionPane.showMessageDialog(this, "Exported to " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}