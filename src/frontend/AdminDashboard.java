package frontend;

import javax.swing.*;
import java.awt.*;
import backend.models.User;

public class AdminDashboard extends JFrame {

    private User user;

    public AdminDashboard(User user) {
        this.user = user;
        setTitle("Admin Dashboard - Quiz Management System");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBackground(UIUtils.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

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
            // open registration dialog to create new users (admin)
            SwingUtilities.invokeLater(() -> {
                RegisterPage dlg = new RegisterPage(this);
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
}