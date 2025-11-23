package frontend;

import javax.swing.*;
import java.awt.*;
import backend.models.User;
import backend.controllers.UserController;
import backend.services.AuthenticationService;

public class TeacherDashboard extends JFrame {

    private User user;
    private UserController userController = new UserController();
    private AuthenticationService authService = new AuthenticationService();

    public TeacherDashboard(User user) {
        this.user = user;
        setTitle("Teacher Dashboard - Quiz Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(16,16));
        panel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        panel.setBackground(UIUtils.BACKGROUND);

        JLabel welcome = new JLabel("Welcome, " + user.getName() + " (Teacher)");
        welcome.setFont(UIUtils.TITLE_FONT);
        welcome.setForeground(UIUtils.PRIMARY);
        panel.add(welcome, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0,1,12,12));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 200, 20, 200));

        JButton createQuizBtn = new JButton("Create Quiz");
        UIUtils.applyPrimaryButton(createQuizBtn);
        createQuizBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> new CreateQuizPage(this).setVisible(true)));

        JButton assignQuizBtn = new JButton("Assign Quiz");
        UIUtils.applyPrimaryButton(assignQuizBtn);
        assignQuizBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> new AssignQuizPage(this).setVisible(true)));

        JButton manageQuestionsBtn = new JButton("Manage Questions");
        UIUtils.applySecondaryButton(manageQuestionsBtn);
        manageQuestionsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Manage Questions - feature coming soon.", "Questions", JOptionPane.INFORMATION_MESSAGE));

        JButton viewResultsBtn = new JButton("View Results");
        UIUtils.applySecondaryButton(viewResultsBtn);
        viewResultsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "View Results - feature coming soon.", "Results", JOptionPane.INFORMATION_MESSAGE));

        JButton changePwdBtn = new JButton("Change Password");
        UIUtils.applySecondaryButton(changePwdBtn);
        changePwdBtn.addActionListener(e -> changePasswordSelf());

        center.add(createQuizBtn);
        center.add(assignQuizBtn);
        center.add(manageQuestionsBtn);
        center.add(viewResultsBtn);
        center.add(changePwdBtn);

        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton logoutBtn = new JButton("Logout");
        UIUtils.applySecondaryButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });
        bottom.add(logoutBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);
    }

    private void changePasswordSelf() {
        // ask for current password first
        JPasswordField currentPf = new JPasswordField();
        int cur = JOptionPane.showConfirmDialog(this, currentPf, "Enter current password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (cur != JOptionPane.OK_OPTION) return;
        String current = new String(currentPf.getPassword());

        // verify current password
        if (authService.login(user.getEmail(), current) == null) {
            JOptionPane.showMessageDialog(this, "password incoorrect ", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // proceed to ask for new password
        JPasswordField pf1 = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, pf1, "Enter new password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        String p1 = new String(pf1.getPassword());
        if (p1.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JPasswordField pf2 = new JPasswordField();
        int ok2 = JOptionPane.showConfirmDialog(this, pf2, "Confirm new password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok2 != JOptionPane.OK_OPTION) return;
        String p2 = new String(pf2.getPassword());
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean result = userController.changePassword(user.getUserId(), p1, Session.getInstance().getCurrentUser());
        if (result) {
            JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}