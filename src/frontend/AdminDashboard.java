package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
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
        // Main panel with gradient background
        JPanel root = new JPanel(new BorderLayout(24, 24)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(249, 250, 251),
                    0, getHeight(), new Color(237, 233, 254)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2.setColor(new Color(199, 210, 254, 40));
                g2.fillOval(-50, -50, 200, 200);
                g2.setColor(new Color(252, 231, 243, 50));
                g2.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);
                
                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Top header with user info
        JPanel header = createHeader();
        root.add(header, BorderLayout.NORTH);

        // Center content with cards grid
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Create cards container with grid layout
        JPanel cardsContainer = new JPanel(new GridBagLayout());
        cardsContainer.setOpaque(false);
        
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(12, 12, 12, 12);
        cardGbc.fill = GridBagConstraints.BOTH;
        cardGbc.weightx = 1;
        cardGbc.weighty = 1;
        
        // Create three cards in a row
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardsContainer.add(createActionCard(
            "Manage Users",
            "Add, edit, and remove user accounts",
            "👥",
            new Color(99, 102, 241),
            e -> SwingUtilities.invokeLater(() -> new ManageUsersPage(this).setVisible(true))
        ), cardGbc);
        
        cardGbc.gridx = 1;
        cardsContainer.add(createActionCard(
            "Manage Quizzes",
            "Create and organize quiz content",
            "📝",
            new Color(236, 72, 153),
            e -> SwingUtilities.invokeLater(() -> new CreateQuizPage(this).setVisible(true))
        ), cardGbc);
        
        cardGbc.gridx = 2;
        cardsContainer.add(createActionCard(
            "View Reports",
            "Analytics and quiz results",
            "📊",
            new Color(168, 85, 247),
            e -> SwingUtilities.invokeLater(() -> new ResultPage(this).setVisible(true))
        ), cardGbc);
        
        centerPanel.add(cardsContainer, gbc);
        root.add(centerPanel, BorderLayout.CENTER);

        add(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 16));
        header.setOpaque(false);
        
        // Left side - welcome message
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Inter", Font.BOLD, 32));
        title.setForeground(new Color(15, 23, 42)); // Dark text
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Manage your quiz platform");
        subtitle.setFont(UIUtils.REGULAR_FONT);
        subtitle.setForeground(new Color(71, 85, 105)); // Secondary text
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(subtitle);
        
        // Right side - user info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        rightPanel.setOpaque(false);
        
        // User info
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName());
        welcomeLabel.setFont(UIUtils.REGULAR_FONT);
        welcomeLabel.setForeground(new Color(71, 85, 105));
        
        // Logout button
        JButton logoutBtn = new JButton("Logout");
        UIUtils.applySecondaryButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            Session.getInstance().clear();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });
        
        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutBtn);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }

    private JPanel createActionCard(String title, String description, String icon, Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }
                    
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        action.actionPerformed(null);
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                int shadowSize = hovered ? 25 : 20;
                float shadowAlpha = hovered ? 0.15f : 0.1f;
                
                for (int i = 0; i < shadowSize; i++) {
                    float alpha = (1.0f - (float) i / shadowSize) * shadowAlpha;
                    g2.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
                    g2.fillRoundRect(i, i + 3, getWidth() - i * 2, getHeight() - i * 2, 20, 20);
                }
                
                // Card background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Accent bar at top
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), 6, 20, 20);
                
                // Border
                g2.setColor(hovered ? accentColor : new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(hovered ? 2 : 1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                // Subtle scale effect
                if (hovered) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                
                g2.dispose();
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(48, 32, 48, 32));
        card.setPreferredSize(new Dimension(300, 250));
        
        // Main content container
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Icon circle
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, accentColor,
                    getWidth(), getHeight(), new Color(
                        Math.min(accentColor.getRed() + 30, 255),
                        Math.min(accentColor.getGreen() + 30, 255),
                        Math.min(accentColor.getBlue() + 30, 255)
                    )
                );
                g2.setPaint(gradient);
                g2.fillOval(0, 0, 80, 80);
                
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(80, 80));
        iconPanel.setMaximumSize(new Dimension(80, 80));
        iconPanel.setLayout(new GridBagLayout());
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconPanel.add(iconLabel);
        
        contentPanel.add(iconPanel);
        contentPanel.add(Box.createVerticalStrut(24));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createVerticalStrut(8));
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        descLabel.setForeground(new Color(71, 85, 105));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel);
        
        card.add(contentPanel);
        
        return card;
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