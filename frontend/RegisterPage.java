package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.controllers.AuthController;
import backend.enums.Role;

public class RegisterPage extends JDialog {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JComboBox<Role> roleCombo;
    private final JFrame parent;
    private AuthController authController = new AuthController();

    public RegisterPage(JFrame parent) {
        super(parent, "Register - Quiz Management System", true);
        this.parent = parent;
        setSize(550, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Outer panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Card
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);

        JLabel title = new JLabel("Create an Account");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(title, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(nameLabel, c);
        usernameField = new JTextField(20);
        usernameField.setFont(UIUtils.REGULAR_FONT);
        // improve visibility and size
        usernameField.setPreferredSize(new Dimension(260, 28));
        usernameField.setCaretColor(UIUtils.PRIMARY);
        usernameField.setForeground(Color.BLACK);
        usernameField.setBackground(Color.WHITE);
        usernameField.setOpaque(true);
        c.gridx = 1;
        card.add(usernameField, c);

        c.gridx = 0; c.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(emailLabel, c);
        emailField = new JTextField(20);
        emailField.setFont(UIUtils.REGULAR_FONT);
        emailField.setPreferredSize(new Dimension(260, 28));
        emailField.setCaretColor(UIUtils.PRIMARY);
        emailField.setForeground(Color.BLACK);
        emailField.setBackground(Color.WHITE);
        emailField.setOpaque(true);
        c.gridx = 1;
        card.add(emailField, c);

        c.gridx = 0; c.gridy = 3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(passLabel, c);
        passwordField = new JPasswordField(20);
        passwordField.setFont(UIUtils.REGULAR_FONT);
        passwordField.setPreferredSize(new Dimension(260, 28));
        passwordField.setCaretColor(UIUtils.PRIMARY);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(Color.WHITE);
        passwordField.setOpaque(true);
        c.gridx = 1;
        card.add(passwordField, c);

        c.gridx = 0; c.gridy = 4;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(confirmLabel, c);
        confirmField = new JPasswordField(20);
        confirmField.setFont(UIUtils.REGULAR_FONT);
        confirmField.setPreferredSize(new Dimension(260, 28));
        confirmField.setCaretColor(UIUtils.PRIMARY);
        confirmField.setForeground(Color.BLACK);
        confirmField.setBackground(Color.WHITE);
        confirmField.setOpaque(true);
        c.gridx = 1;
        card.add(confirmField, c);

        c.gridx = 0; c.gridy = 5;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(UIUtils.REGULAR_FONT);
        card.add(roleLabel, c);
        roleCombo = new JComboBox<>(Role.values());
        roleCombo.setPreferredSize(new Dimension(260, 28));
        c.gridx = 1;
        card.add(roleCombo, c);

        JButton registerBtn = new JButton("Register");
        UIUtils.applyPrimaryButton(registerBtn);
        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        UIUtils.applySecondaryButton(cancelBtn);
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                parent.setVisible(true);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(registerBtn);
        btnPanel.add(cancelBtn);

        c.gridx = 0; c.gridy = 6; c.gridwidth = 2;
        card.add(btnPanel, c);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(card, gbc);

        add(panel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                parent.setVisible(true);
            }
            public void windowClosed(WindowEvent e) {
                parent.setVisible(true);
            }
        });
    }

    private void performRegister() {
        String name = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        Role role = (Role) roleCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!UIUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Call backend AuthController to register
        boolean ok = authController.register(name, email, pass, role);

        if (!ok) {
            JOptionPane.showMessageDialog(this, "Registration failed. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration successful. You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        parent.setVisible(true);
    }
}