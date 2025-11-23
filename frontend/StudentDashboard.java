package frontend;

import javax.swing.*;
import java.awt.*;
import backend.models.User;

public class StudentDashboard extends JFrame {

    private User user;

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

        center.add(attemptQuizBtn);
        center.add(viewResultsBtn);
        center.add(myProfileBtn);

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
}