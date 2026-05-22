package gui;

import service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class UserManagementPanel extends JPanel {
    private final UserService userService;
    private final Runnable onDataChanged;

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JLabel totalUsersLabel;

    public UserManagementPanel(UserService userService, Runnable onDataChanged) {
        this.userService = userService;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(18, 18));
        setBackground(UIHelper.APP_BG);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(createHeader(), BorderLayout.NORTH);
        add(createTableCard(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel createHeader() {
        JPanel header = UIHelper.createTransparentPanel();
        header.setLayout(new BorderLayout());

        JPanel titleBox = UIHelper.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitleLabel("User Management");
        JLabel subtitle = UIHelper.createSubtitleLabel("Admin can add or delete system users");

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JPanel actions = UIHelper.createTransparentPanel();
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton addBtn = UIHelper.createButton("Add User");
        addBtn.addActionListener(e -> addUser());

        JButton deleteBtn = UIHelper.createButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelectedUser());

        JButton refreshBtn = UIHelper.createButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        actions.add(addBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        header.add(titleBox, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JPanel createTableCard() {
        JPanel card = UIHelper.createCardPanel();

        JLabel cardTitle = UIHelper.createSectionLabel("Registered Users");
        cardTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        String[] columns = {"Username", "Role", "Password"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        UIHelper.styleTable(userTable);

        userTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(160);

        JScrollPane scrollPane = UIHelper.wrapScroll(userTable, UIHelper.PANEL_BG);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = UIHelper.createTransparentPanel();
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));

        totalUsersLabel = new JLabel("Total Users: 0");
        totalUsersLabel.setForeground(UIHelper.TEXT_MUTED);
        totalUsersLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        footer.add(totalUsersLabel);
        return footer;
    }

    private void addUser() {
        String username = JOptionPane.showInputDialog(this, "Enter Username:");
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        String password = JOptionPane.showInputDialog(this, "Enter Password:");
        if (password == null || password.trim().isEmpty()) {
            return;
        }

        String[] roles = {"ADMIN", "STAFF"};
        String role = (String) JOptionPane.showInputDialog(
                this,
                "Select Role:",
                "Role",
                JOptionPane.PLAIN_MESSAGE,
                null,
                roles,
                "STAFF"
        );

        if (role == null || role.trim().isEmpty()) {
            return;
        }

        if (userService.userExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
            return;
        }

        boolean added = userService.addUser(username.trim(), password.trim(), role.trim());
        if (added) {
            refreshTable();
            if (onDataChanged != null) {
                onDataChanged.run();
            }
            JOptionPane.showMessageDialog(this, "User added successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user.");
        }
    }

    private void deleteSelectedUser() {
        int selectedViewRow = userTable.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user row first.");
            return;
        }

        int selectedModelRow = userTable.convertRowIndexToModel(selectedViewRow);
        String username = tableModel.getValueAt(selectedModelRow, 0).toString();

        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "Admin account cannot be deleted.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete user " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            userService.removeUser(username);
            refreshTable();
            if (onDataChanged != null) {
                onDataChanged.run();
            }
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        for (Map.Entry<String, String> entry : userService.getAllUsers().entrySet()) {
            String username = entry.getKey();
            String role = entry.getValue();
            String password = userService.getPassword(username);

            tableModel.addRow(new Object[]{username, role, password});
        }

        totalUsersLabel.setText("Total Users: " + userService.getAllUsers().size());
        revalidate();
        repaint();
    }
}