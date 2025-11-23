package frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;

public class UIUtils {

    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color PRIMARY = new Color(30, 115, 190);
    public static final Color ACCENT = new Color(120, 220, 140);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static void applyPrimaryButton(JButton b) {
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(REGULAR_FONT);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void applySecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(PRIMARY);
        b.setFocusPainted(false);
        b.setFont(REGULAR_FONT);
        b.setBorder(BorderFactory.createLineBorder(PRIMARY, 1));
    }

    public static JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return p;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_REGEX.matcher(email).matches();
    }
}