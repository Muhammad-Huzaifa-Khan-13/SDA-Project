package frontend;

import backend.controllers.ReportController;
import backend.dao.UserDAO;
import backend.models.Report;
import backend.models.User;
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
    private UserDAO userDAO = new UserDAO();
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

        // Table
        tableModel = new DefaultTableModel(new Object[]{"Report ID","Quiz ID","Total Q","Correct","%"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
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
            // populate studentCombo with students
            List<User> users = userDAO.getAll();
            DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
            if (users != null) {
                for (User u : users) {
                    if (u.getRole() == Role.STUDENT) model.addElement(u);
                }
            }
            studentCombo.setModel(model);
            if (model.getSize() > 0) studentCombo.setSelectedIndex(0);
            refreshTable();
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
                    tableModel.addRow(new Object[]{r.getReportId(), r.getQuizId(), r.getTotalQuestions(), r.getCorrectAnswers(), r.getPercentage()});
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

        int reportId = (int) tableModel.getValueAt(row, 0);
        int quizId = (int) tableModel.getValueAt(row, 1);
        // We know which student is currently displayed
        int studentId = displayedStudentId;

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
        if (row >= 0) quizId = (int) tableModel.getValueAt(row, 1);

        if (quizId == null) {
            String s = JOptionPane.showInputDialog(this, "Enter Quiz ID to export:", "Quiz ID", JOptionPane.PLAIN_MESSAGE);
            if (s == null) return;
            try { quizId = Integer.parseInt(s.trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid quiz id.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        }

        List<Report> reports = getReportsForQuiz(quizId);
        if (reports == null || reports.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No reports found for quiz id " + quizId, "Info", JOptionPane.INFORMATION_MESSAGE);
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
            if (choice == 0) {
                // CSV header
                fw.write("report_id,student_id,quiz_id,total_questions,correct_answers,percentage\n");
                for (Report r : reports) {
                    fw.write(String.format("%d,%d,%d,%d,%d,%.2f\n", r.getReportId(), r.getStudentId(), r.getQuizId(), r.getTotalQuestions(), r.getCorrectAnswers(), r.getPercentage()));
                }
            } else {
                // TXT human readable
                for (Report r : reports) {
                    fw.write("Report ID: " + r.getReportId() + "\n");
                    fw.write("Student ID: " + r.getStudentId() + "\n");
                    fw.write("Quiz ID: " + r.getQuizId() + "\n");
                    fw.write("Total Questions: " + r.getTotalQuestions() + "\n");
                    fw.write("Correct Answers: " + r.getCorrectAnswers() + "\n");
                    fw.write(String.format("Percentage: %.2f%%\n", r.getPercentage()));
                    fw.write("----------------------------------------\n");
                }
            }
            fw.flush();
            JOptionPane.showMessageDialog(this, "Exported to " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}