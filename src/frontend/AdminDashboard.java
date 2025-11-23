package frontend;

import javax.swing.*;
import java.awt.*;
import backend.models.User;
import backend.controllers.UserController;

public class AdminDashboard extends JFrame {

    private User user;

    public AdminDashboard(User user) {
        this.user = user;
        setTitle("Admin Dashboard - Quiz Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(16,16));
        root.setBackground(UIUtils.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

        // Top header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        header.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Signed in as: " + user.getName());
        sub.setFont(UIUtils.REGULAR_FONT);
        header.add(sub, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // Center card with actions
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JButton manageUsersBtn = new JButton("Manage Users");
        UIUtils.applyPrimaryButton(manageUsersBtn);
        manageUsersBtn.addActionListener(e -> {
            // open ManageUsersPage (admin) to manage and remove users
            SwingUtilities.invokeLater(() -> {
                ManageUsersPage dlg = new ManageUsersPage(this);
                dlg.setVisible(true);
            });
        });

        JButton manageQuizzesBtn = new JButton("Manage Quizzes");
        UIUtils.applyPrimaryButton(manageQuizzesBtn);
        manageQuizzesBtn.addActionListener(e -> {
            // open CreateQuizPage
            SwingUtilities.invokeLater(() -> {
                CreateQuizPage dlg = new CreateQuizPage(this);
                dlg.setVisible(true);
            });
        });

        JButton viewReportsBtn = new JButton("View Reports");
        UIUtils.applyPrimaryButton(viewReportsBtn);
        viewReportsBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                ResultPage dlg = new ResultPage(this);
                dlg.setVisible(true);
            });
        });

        c.gridx = 0; c.gridy = 0;
        card.add(manageUsersBtn, c);
        c.gridy = 1;
        card.add(manageQuizzesBtn, c);
        c.gridy = 2;
        card.add(viewReportsBtn, c);

        root.add(card, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton logoutBtn = new JButton("Logout");
        UIUtils.applySecondaryButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            Session.getInstance().clear();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });
        bottom.add(logoutBtn);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    // Allow admin to change password for any user
    public void changePasswordForUser(int userId) {
        User current = Session.getInstance().getCurrentUser();
        if (current == null || current.getRole() != backend.enums.Role.ADMIN) {
            JOptionPane.showMessageDialog(this, "Only admin can change other users' passwords.", "Permission", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPasswordField pf1 = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, pf1, "Enter new password for user (ID: " + userId + "):", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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

        UserController uc = new UserController();
        boolean res = uc.changePassword(userId, p1, current);
        if (res) JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(this, "Failed to change password.", "Error", JOptionPane.ERROR_MESSAGE);
    }

}