package frontend;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static final Color BACKGROUND = new Color(245, 245, 245);
    public static final Color PRIMARY = new Color(52, 152, 219);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    public static void applyPrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(REGULAR_FONT);
        btn.setFocusPainted(false);
    }

    public static void applySecondaryButton(JButton btn) {
        btn.setBackground(new Color(230, 230, 230));
        btn.setForeground(Color.BLACK);
        btn.setFont(REGULAR_FONT);
        btn.setFocusPainted(false);
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
