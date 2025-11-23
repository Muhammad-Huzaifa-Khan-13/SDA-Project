package frontend;

import backend.controllers.UserController;
import backend.enums.Role;
import backend.models.User;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPage extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private UserController userController = new UserController();
    private JComboBox<String> filterCombo;

    public ManageUsersPage(JFrame parent) {
        super(parent, "Manage Users", true);
        setSize(700, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        initUI();
        loadUsers();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBackground(UIUtils.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        JLabel title = new JLabel("Manage Users");
        title.setFont(UIUtils.TITLE_FONT);
        title.setForeground(UIUtils.PRIMARY);
        top.add(title);

        top.add(new JLabel("  Show: "));
        filterCombo = new JComboBox<>(new String[]{"All","Students","Teachers"});
        filterCombo.setPreferredSize(new Dimension(160, 28));
        filterCombo.addActionListener(e -> loadUsers());
        top.add(filterCombo);

        root.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID","Name","Email","Role"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton deleteBtn = new JButton("Delete Selected");
        UIUtils.applyPrimaryButton(deleteBtn);
        deleteBtn.addActionListener(e -> onDelete());

        JButton changePwdBtn = new JButton("Change Password");
        UIUtils.applySecondaryButton(changePwdBtn);
        changePwdBtn.addActionListener(e -> onChangePassword());

        JButton closeBtn = new JButton("Close");
        UIUtils.applySecondaryButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());

        bottom.add(deleteBtn);
        bottom.add(changePwdBtn);
        bottom.add(closeBtn);

        root.add(bottom, BorderLayout.SOUTH);

        add(root);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userController.getAllUsers();
            if (users == null) return;

            String filter = (String) filterCombo.getSelectedItem();
            for (User u : users) {
                if (filter.equals("Students") && u.getRole() != Role.STUDENT) continue;
                if (filter.equals("Teachers") && u.getRole() != Role.TEACHER) continue;
                tableModel.addRow(new Object[]{u.getUserId(), u.getName(), u.getEmail(), u.getRole().name()});
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        String roleStr = (String) tableModel.getValueAt(row, 3);
        Role targetRole = Role.valueOf(roleStr);

        // don't allow deleting admins from this UI
        if (targetRole == Role.ADMIN) {
            JOptionPane.showMessageDialog(this, "Cannot delete admin users from here.", "Permission", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User current = Session.getInstance().getCurrentUser();
        if (current == null) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (current.getUserId() == userId) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = userController.deleteUser(userId, current);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Failed to delete user. Ensure you have permissions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        loadUsers();
    }

    private void onChangePassword() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to change password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);

        // delegate to AdminDashboard's helper if owner is admin
        if (getOwner() instanceof AdminDashboard) {
            AdminDashboard admin = (AdminDashboard) getOwner();
            admin.changePasswordForUser(userId);
            loadUsers();
            return;
        }

        // fallback: use controller directly
        User current = Session.getInstance().getCurrentUser();
        if (current == null || current.getRole() != Role.ADMIN) {
            JOptionPane.showMessageDialog(this, "Only admin can change other users' passwords.", "Permission", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPasswordField pf1 = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, pf1, "Enter new password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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

        boolean res = userController.changePassword(userId, p1, current);
        if (res) JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(this, "Failed to change password.", "Error", JOptionPane.ERROR_MESSAGE);

        loadUsers();
    }
}