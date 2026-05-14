package frontend;

import backend.models.Quiz;
import backend.models.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class UIUtils {

    // Use Eternex primary purple across the project
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color PRIMARY = new Color(0x99, 0x00, 0x66); // previously blue
    public static final Color ACCENT = new Color(0xCC, 0x66, 0x99);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
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

    // Deduplicate quizzes by title (case-insensitive, trimmed). Preserves first occurrence order.
    public static List<Quiz> dedupeQuizzesByTitle(List<Quiz> quizzes) {
        List<Quiz> out = new ArrayList<>();
        if (quizzes == null || quizzes.isEmpty()) return out;
        LinkedHashMap<String, Quiz> map = new LinkedHashMap<>();
        for (Quiz q : quizzes) {
            if (q == null) continue;
            String title = q.getTitle() != null ? q.getTitle().trim().toLowerCase() : null;
            if (title == null || title.isEmpty()) {
                // use id as key for untitled quizzes to avoid clobbering
                title = "__untitled__" + q.getQuizId();
            }
            if (!map.containsKey(title)) map.put(title, q);
        }
        out.addAll(map.values());
        return out;
    }

    // Deduplicate courses by name (case-insensitive, trimmed). Preserves first occurrence.
    public static List<Course> dedupeCoursesByName(List<Course> courses) {
        List<Course> out = new ArrayList<>();
        if (courses == null || courses.isEmpty()) return out;
        LinkedHashMap<String, Course> map = new LinkedHashMap<>();
        for (Course c : courses) {
            if (c == null) continue;
            String name = c.getCourseName() != null ? c.getCourseName().trim().toLowerCase() : null;
            if (name == null || name.isEmpty()) {
                name = "__course__" + c.getCourseId();
            }
            if (!map.containsKey(name)) map.put(name, c);
        }
        out.addAll(map.values());
        return out;
    }

    // ...existing nested classes (RoundedPanel, RoundedButton, etc.)
}