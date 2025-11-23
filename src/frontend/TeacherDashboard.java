package frontend;

import javax.swing.*;
import java.awt.*;
import backend.models.User;

public class TeacherDashboard extends JFrame {

    private User user;

    public TeacherDashboard(User user) {
        this.user = user;
        setTitle("Teacher Dashboard - Quiz Management System");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel welcome = new JLabel("Welcome, " + user.getName() + " (Teacher)");
        welcome.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcome, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0,1,5,5));

        JButton createQuizBtn = new JButton("Create Quiz");
        createQuizBtn.addActionListener(e -> {
            // open create quiz dialog
            SwingUtilities.invokeLater(() -> {
                new CreateQuizPage(this).setVisible(true);
            });
        });

        JButton assignQuizBtn = new JButton("Assign Quiz");
        assignQuizBtn.addActionListener(e -> {
            // open assign quiz dialog
            SwingUtilities.invokeLater(() -> {
                new AssignQuizPage(this).setVisible(true);
            });
        });

        JButton manageQuestionsBtn = new JButton("Manage Questions");
        manageQuestionsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Manage Questions - feature coming soon.", "Questions", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton viewResultsBtn = new JButton("View Results");
        viewResultsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "View Results - feature coming soon.", "Results", JOptionPane.INFORMATION_MESSAGE);
        });

        center.add(createQuizBtn);
        center.add(assignQuizBtn);
        center.add(manageQuestionsBtn);
        center.add(viewResultsBtn);

        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });
        bottom.add(logoutBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);
    }
}