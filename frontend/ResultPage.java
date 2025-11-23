package frontend;

import backend.controllers.ReportController;
import backend.dao.UserDAO;
import backend.models.Report;
import backend.models.User;
import backend.enums.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultPage extends JDialog {

    private JComboBox<User> studentCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private ReportController reportController = new ReportController();
    private UserDAO userDAO = new UserDAO();

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
        JButton close = new JButton("Close");
        UIUtils.applySecondaryButton(close);
        close.addActionListener(e -> dispose());
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
}