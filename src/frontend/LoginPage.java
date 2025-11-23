package frontend;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private backend.controllers.AuthController authController = new backend.controllers.AuthController();
    private RoundedButton loginBtn;
    private RoundedButton registerBtn;

    public LoginPage() {
        initUI();
    }

    private void initUI() {
        setTitle("Quiz Management System - Login");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Main container with two columns
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Color.WHITE);
        add(container, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        // LEFT PANEL - card with inputs
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
        // Use BorderLayout and vertical BoxLayout inside to match RegisterPage alignment
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(520, 560));
        card.setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 220), 1, true), new EmptyBorder(24, 24, 24, 24)));

        // Title
        JLabel title = new JLabel("Sign in to your account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(6, 6, 12, 6));
        card.add(title, BorderLayout.NORTH);

        // Form - vertical layout similar to RegisterPage
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel emailLbl = new JLabel("Email");
        emailLbl.setFont(UIUtils.REGULAR_FONT);
        emailLbl.setForeground(Color.DARK_GRAY);
        emailLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(emailLbl);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        usernameField = new JTextField();
        styleField(usernameField);
        usernameField.setToolTipText("Email");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UIUtils.REGULAR_FONT);
        passLbl.setForeground(Color.DARK_GRAY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passLbl);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        passwordField = new JPasswordField();
        styleField(passwordField);
        passwordField.setToolTipText("Password");
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordField);

        // center form inside wrapper for padding
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel);
        card.add(wrapper, BorderLayout.CENTER);

        // Bottom area: login button and register row
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnWrap.setOpaque(false);
        loginBtn = new RoundedButton("Login", UIUtils.PRIMARY.brighter());
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setPreferredSize(new Dimension(260, 48));
        loginBtn.setEnabled(false);
        btnWrap.add(loginBtn);
        bottomPanel.add(btnWrap);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel registerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        registerArea.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noAccount.setForeground(Color.DARK_GRAY);
        registerBtn = new RoundedButton("Register", UIUtils.PRIMARY);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerBtn.setPreferredSize(new Dimension(120, 34));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerArea.add(noAccount);
        registerArea.add(registerBtn);
        bottomPanel.add(registerArea);

        card.add(bottomPanel, BorderLayout.SOUTH);

        leftHolder.add(card, l);

        // RIGHT PANEL - curved background with logo and texts (same as SignUp)
        JPanel rightPanel = new CurvedPanel(UIUtils.PRIMARY);
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        container.add(rightPanel, gbc);

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0;
        rc.gridy = 0;
        // center contents vertically and horizontally for better visual balance
        rc.anchor = GridBagConstraints.CENTER;
        rc.insets = new Insets(60, 0, 10, 0);

        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = loadLogo();
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("E");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
            logoLabel.setForeground(Color.WHITE);
        }
        rightPanel.add(logoLabel, rc);

        rc.gridy = 1;
        rc.insets = new Insets(28, 0, 6, 0);
        JLabel name = new JLabel("Eternex");
        name.setForeground(Color.BLACK);
        name.setFont(new Font("Segoe UI", Font.BOLD, 44));
        rightPanel.add(name, rc);

        rc.gridy = 2;
        JLabel welcome = new JLabel("Welcome to Quiz Management System");
        welcome.setForeground(Color.BLACK);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        rightPanel.add(welcome, rc);

        // interactions: enable login when fields filled
        DocumentListener dl = new DocumentListener() {
            private void update() {
                boolean ok = !usernameField.getText().trim().isEmpty() && passwordField.getPassword().length > 0;
                loginBtn.setEnabled(ok);
            }

            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        };
        usernameField.getDocument().addDocumentListener(dl);
        passwordField.getDocument().addDocumentListener(dl);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
            }
        });

        // set default button
        getRootPane().setDefaultButton(loginBtn);

        setVisible(true);
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
                        Session.getInstance().setCurrentUser(user);
                        JOptionPane.showMessageDialog(LoginPage.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    private void styleField(JTextField f) {
        // larger, professional input appearance with focus highlight
        f.setBackground(new Color(0xF1, 0xF1, 0xF1));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true), new EmptyBorder(12, 14, 12, 14)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        // match RegisterPage widths for consistent alignment
        f.setPreferredSize(new Dimension(420, 44));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setCaretColor(UIUtils.PRIMARY);
        // focus effect
        f.addFocusListener(new FocusAdapter() {
            private final Border defaultBorder = f.getBorder();
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(UIUtils.PRIMARY, 2, true), new EmptyBorder(10, 12, 10, 12)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(defaultBorder);
            }
        });
    }

    private ImageIcon loadLogo() {
        try {
            String path = System.getProperty("user.dir") + File.separator + "eternex.png";
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) return null;
            int w = 220; // larger logo for better visibility
            int h = (int) (img.getHeight() * (w / (double) img.getWidth()));
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            return null;
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

    // Curved right panel
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
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}