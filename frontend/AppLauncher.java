package frontend;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        // Set a modern Look & Feel (Nimbus) if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // ignore and use default
        }

        // Start UI on EDT
        SwingUtilities.invokeLater(() -> {
            // small UI tweaks can be applied here if needed
            new LoginPage().setVisible(true);
        });
    }
}