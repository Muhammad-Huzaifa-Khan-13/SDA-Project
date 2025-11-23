package frontend;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import backend.controllers.AuthController;
import backend.enums.Role;

public class RegisterPage extends JFrame {

    private PlaceholderTextField nameField;
    private PlaceholderTextField emailField;
    private PlaceholderTextField confirmEmailField;
    private PlaceholderPasswordField passwordField;
    private PlaceholderPasswordField confirmField;
    private JComboBox<Role> roleCombo;

    private AuthController authController = new AuthController();

    public RegisterPage() {
        initUI();
    }

    private void initUI() {
        setTitle("Register - Eternex");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Main container with two columns using GridBag for responsiveness
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Color.WHITE);
        add(container, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        // LEFT: form card
        JPanel leftHolder = new JPanel(new GridBagLayout());
        leftHolder.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 1.0;
        container.add(leftHolder, gbc);

        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0;
        l.gridy = 0;
        l.anchor = GridBagConstraints.CENTER;
        l.insets = new Insets(30, 30, 30, 30);

        RoundedPanel card = new RoundedPanel(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(520, 560));
        card.setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(24, 24, 24, 24)));

        // Title at top
        JLabel title = new JLabel("Create your account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(6, 6, 12, 6));
        card.add(title, BorderLayout.NORTH);

        // Form panel uses BoxLayout vertically for even spacing
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Helper to add field with spacing
        nameField = new PlaceholderTextField("Full Name");
        styleField(nameField);
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        emailField = new PlaceholderTextField("Email");
        styleField(emailField);
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        confirmEmailField = new PlaceholderTextField("Confirm Email");
        styleField(confirmEmailField);
        formPanel.add(confirmEmailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        passwordField = new PlaceholderPasswordField("Password");
        styleField(passwordField);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        confirmField = new PlaceholderPasswordField("Confirm Password");
        styleField(confirmField);
        formPanel.add(confirmField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        roleCombo = new JComboBox<>(Role.values());
        roleCombo.setPreferredSize(new Dimension(420, 44));
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(roleCombo);

        // center formPanel inside a wrapper to provide padding
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel);
        card.add(wrapper, BorderLayout.CENTER);

        // Register button centered
        final RoundedButton registerBtn = new RoundedButton("Register", UIUtils.PRIMARY.brighter());
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerBtn.setPreferredSize(new Dimension(260, 48));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(registerBtn);
        bottomPanel.add(btnWrap);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Already have account text + clickable Login button
        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        loginRow.setOpaque(false);
        JLabel already = new JLabel("Already have an account?");
        already.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        already.setForeground(Color.DARK_GRAY);
        final JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setForeground(UIUtils.PRIMARY);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginRow.add(already);
        loginRow.add(loginBtn);
        bottomPanel.add(loginRow);

        card.add(bottomPanel, BorderLayout.SOUTH);

        leftHolder.add(card, l);

        // RIGHT PANEL
        JPanel rightPanel = new CurvedPanel(UIUtils.PRIMARY);
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        container.add(rightPanel, gbc);

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0;
        rc.gridy = 0;
        rc.anchor = GridBagConstraints.CENTER;
        rc.weighty = 1.0;
        rc.insets = new Insets(40, 0, 10, 0);

        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = loadLogo();
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("E");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 56));
            logoLabel.setForeground(Color.WHITE);
        }
        rightPanel.add(logoLabel, rc);

        rc.gridy = 1;
        rc.insets = new Insets(22, 0, 6, 0);
        JLabel name = new JLabel("Eternex");
        name.setForeground(Color.BLACK);
        name.setFont(new Font("Segoe UI", Font.BOLD, 44));
        rightPanel.add(name, rc);

        rc.gridy = 2;
        JLabel welcome = new JLabel("Welcome to Quiz Management System");
        welcome.setForeground(Color.BLACK);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        rightPanel.add(welcome, rc);

        // interactions: enable register when fields have content
        DocumentListener dl = new DocumentListener() {
            private void update() {
                boolean ok = !nameField.getText().trim().isEmpty()
                        && !emailField.getText().trim().isEmpty()
                        && !confirmEmailField.getText().trim().isEmpty()
                        && passwordField.getPassword().length > 0
                        && confirmField.getPassword().length > 0;
                registerBtn.setEnabled(ok);
            }

            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        nameField.getDocument().addDocumentListener(dl);
        emailField.getDocument().addDocumentListener(dl);
        confirmEmailField.getDocument().addDocumentListener(dl);
        passwordField.getDocument().addDocumentListener(dl);
        confirmField.getDocument().addDocumentListener(dl);

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });

        // login action: open LoginPage and dispose current (on EDT)
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new LoginPage().setVisible(true);
                });
                dispose();
            }
        });

        add(container);
        setVisible(true);
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String confirmEmail = confirmEmailField.getText().trim();
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

        if (!email.equals(confirmEmail)) {
            JOptionPane.showMessageDialog(this, "Emails do not match", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = authController.register(name, email, pass, role);

        if (!ok) {
            JOptionPane.showMessageDialog(this, "Registration failed. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration successful. You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        new LoginPage().setVisible(true);
        dispose();
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(0xF1, 0xF1, 0xF1));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true), new EmptyBorder(10, 14, 10, 14)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(420, 44));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private ImageIcon loadLogo() {
        try {
            String path = System.getProperty("user.dir") + File.separator + "eternex.png";
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) return null;
            int w = 180;
            int h = (int) (img.getHeight() * (w / (double) img.getWidth()));
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            return null;
        }
    }

    // Simple placeholder-supporting text field
    private static class PlaceholderTextField extends JTextField {
        private String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(150, 150, 150));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets ins = getInsets();
                g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                g2.dispose();
            }
        }
    }

    private static class PlaceholderPasswordField extends JPasswordField {
        private String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(150, 150, 150));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets ins = getInsets();
                g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                g2.dispose();
            }
        }
    }

    // Rounded card panel
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.backgroundColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Rounded button
    private static class RoundedButton extends JButton {
        private final Color bgColor;

        public RoundedButton(String text, Color bg) {
            super(text);
            this.bgColor = bg;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setForeground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isEnabled()) {
                g2.setColor(bgColor);
            } else {
                g2.setColor(bgColor.darker().darker());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {
            // no border
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }

    // Right curved panel
    private static class CurvedPanel extends JPanel {
        private final Color curveColor;

        public CurvedPanel(Color c) {
            this.curveColor = c;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int ovalW = (int) (w * 1.6);
            int ovalH = (int) (h * 1.8);
            int x = w - (ovalW / 3);
            int y = -ovalH / 4;
            g2.setColor(curveColor);
            g2.fill(new Ellipse2D.Double(x, y, ovalW, ovalH));
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
    }
}