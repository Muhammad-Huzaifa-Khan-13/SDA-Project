package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private backend.controllers.AuthController authController = new backend.controllers.AuthController();
    private JButton loginBtn;
    private JButton registerBtn;

    public LoginPage() {
        setTitle("Quiz Management System - Login");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // center screen
        setResizable(false);

        // Main Panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Card container
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel title = new JLabel("Quiz Management System");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(title, c);

        // Username Label
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(emailLabel, c);

        // Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(UIUtils.REGULAR_FONT);
        gbc.gridx = 1;
        c.gridx = 1;
        card.add(usernameField, c);

        // Password Label
        c.gridx = 0; c.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(passLabel, c);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(UIUtils.REGULAR_FONT);
        c.gridx = 1;
        card.add(passwordField, c);

        // Login Button
        loginBtn = new JButton("Login");
        UIUtils.applyPrimaryButton(loginBtn);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Register Button
        registerBtn = new JButton("Register");
        UIUtils.applySecondaryButton(registerBtn);
        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Hide this login window and open register page
                setVisible(false);
                SwingUtilities.invokeLater(() -> {
                    new RegisterPage(LoginPage.this).setVisible(true);
                });
            }
        });

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        card.add(btnPanel, c);

        // place card into outer panel to center it
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(card, gbc);

        add(panel);

        // set default button for Enter key
        getRootPane().setDefaultButton(loginBtn);

        // small accessibility touches
        usernameField.setToolTipText("Enter your email");
        passwordField.setToolTipText("Enter your password");
        loginBtn.setToolTipText("Sign in");
        registerBtn.setToolTipText("Open registration dialog");
    }

    private void login() {
        String email = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!UIUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // run authentication off the EDT
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginBtn.setEnabled(false);
        registerBtn.setEnabled(false);
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);

        SwingWorker<backend.models.User, Void> worker = new SwingWorker<>() {
            @Override
            protected backend.models.User doInBackground() throws Exception {
                return authController.login(email, pass);
            }

            @Override
            protected void done() {
                try {
                    backend.models.User user = get();
                    if (user == null) {
                        JOptionPane.showMessageDialog(LoginPage.this, "Invalid Email or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // store user in session for app-wide access
                        Session.getInstance().setCurrentUser(user);
                        JOptionPane.showMessageDialog(LoginPage.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // Open appropriate dashboard based on role
                        switch (user.getRole()) {
                            case ADMIN:
                                SwingUtilities.invokeLater(() -> new AdminDashboard(user).setVisible(true));
                                break;
                            case TEACHER:
                                SwingUtilities.invokeLater(() -> new TeacherDashboard(user).setVisible(true));
                                break;
                            case STUDENT:
                                SwingUtilities.invokeLater(() -> new StudentDashboard(user).setVisible(true));
                                break;
                            default:
                                SwingUtilities.invokeLater(() -> new StudentDashboard(user).setVisible(true));
                        }
                        dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginPage.this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    loginBtn.setEnabled(true);
                    registerBtn.setEnabled(true);
                    usernameField.setEnabled(true);
                    passwordField.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}