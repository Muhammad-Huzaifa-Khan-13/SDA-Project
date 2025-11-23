package frontend;

import javax.swing.*;
import java.awt.*;
import backend.models.User;
import backend.controllers.UserController;
import backend.services.AuthenticationService;

public class StudentDashboard extends JFrame {

    private User user;
    private UserController userController = new UserController();
    private AuthenticationService authService = new AuthenticationService();

    public StudentDashboard(User user) {
        this.user = user;
        setTitle("Student Dashboard - Quiz Management System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel welcome = new JLabel("Welcome, " + user.getName() + " (Student)");
        welcome.setFont(UIUtils.TITLE_FONT);
        welcome.setForeground(UIUtils.PRIMARY);
        panel.add(welcome, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0,1,10,10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20,100,20,100));

        JButton attemptQuizBtn = new JButton("Attempt Quiz");
        UIUtils.applyPrimaryButton(attemptQuizBtn);
        attemptQuizBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new AttemptQuizPage(this).setVisible(true));
        });

        JButton viewResultsBtn = new JButton("View Results");
        UIUtils.applySecondaryButton(viewResultsBtn);
        viewResultsBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new ResultPage(this).setVisible(true));
        });

        JButton myProfileBtn = new JButton("My Profile");
        UIUtils.applySecondaryButton(myProfileBtn);
        myProfileBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Name: " + user.getName() + "\nEmail: " + user.getEmail(), "Profile", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton changePwdBtn = new JButton("Change Password");
        UIUtils.applySecondaryButton(changePwdBtn);
        changePwdBtn.addActionListener(e -> changePasswordSelf());

        center.add(attemptQuizBtn);
        center.add(viewResultsBtn);
        center.add(myProfileBtn);
        center.add(changePwdBtn);

        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        UIUtils.applySecondaryButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            Session.getInstance().clear();
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