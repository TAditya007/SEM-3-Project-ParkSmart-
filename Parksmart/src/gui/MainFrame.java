package gui;

import service.AnalyticsService;
import service.ReservationService;
import service.RouteService;
import service.SlotService;
import service.VehicleService;
import service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private CardLayout rootLayout;
    private JPanel rootPanel;

    private JPanel appShell;
    private JPanel sidebarPanel;
    private JPanel contentHost;

    private CardLayout contentLayout;
    private JPanel contentPanel;

    private String currentRole = "";

    private final SlotService slotService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final RouteService routeService;
    private final AnalyticsService analyticsService;
    private final UserService userService;

    private DashboardPanel dashboardPanel;
    private SlotPanel slotPanel;
    private VehiclePanel vehiclePanel;
    private ReservationPanel reservationPanel;
    private RoutePanel routePanel;
    private AnalyticsPanel analyticsPanel;
    private UserManagementPanel userManagementPanel;

    private JTextField usernameField;
    private JPasswordField passwordField;

    private final Map<String, String> adminAccounts = new HashMap<>();
    private final Map<String, String> staffAccounts = new HashMap<>();

    public MainFrame() {
        slotService = new SlotService();
        vehicleService = new VehicleService();
        reservationService = new ReservationService();
        routeService = new RouteService();
        analyticsService = new AnalyticsService();
        userService = new UserService();

        initializeAccounts();

        setTitle("ParkSmart - Smart Parking Management");
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

        rootPanel.add(createWelcomeScreen(), "WELCOME");
        rootPanel.add(createLoginScreen(), "LOGIN");

        appShell = createAppShell();
        rootPanel.add(appShell, "APP");

        add(rootPanel);
        rootLayout.show(rootPanel, "WELCOME");
    }

    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 18, 32),
                        getWidth(), getHeight(), new Color(24, 44, 72)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(0, 255, 200, 30));
                g2.fillOval(getWidth() - 350, 70, 240, 240);
                g2.setColor(new Color(0, 140, 255, 35));
                g2.fillOval(80, getHeight() - 280, 260, 260);
            }
        };

        JPanel center = UIHelper.createTransparentPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(160, 100, 120, 100));

        JLabel title = new JLabel("ParkSmart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Modern parking control system for admins and staff");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        sub.setForeground(new Color(210, 220, 235));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton startBtn = createLargeAccentButton("Continue");
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        startBtn.addActionListener(e -> rootLayout.show(rootPanel, "LOGIN"));

        center.add(title);
        center.add(Box.createVerticalStrut(14));
        center.add(sub);
        center.add(Box.createVerticalStrut(30));
        center.add(startBtn);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(11, 18, 30));

        JPanel card = UIHelper.createCardPanel();
        card.setPreferredSize(new Dimension(460, 380));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 210, 255), 2, true),
                new EmptyBorder(28, 28, 28, 28)
        ));

        JLabel heading = new JLabel("Login Access");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel info = new JLabel("Enter your username and password");
        info.setForeground(UIHelper.TEXT_MUTED);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel userBlock = new JPanel();
        userBlock.setLayout(new BoxLayout(userBlock, BoxLayout.Y_AXIS));
        userBlock.setOpaque(false);
        userBlock.setAlignmentX(Component.CENTER_ALIGNMENT);
        userBlock.setMaximumSize(new Dimension(380, 90));

        JLabel userLabel = UIHelper.createFieldLabel("Username");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField = UIHelper.createTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        userBlock.add(userLabel);
        userBlock.add(Box.createVerticalStrut(6));
        userBlock.add(usernameField);

        JPanel passBlock = new JPanel();
        passBlock.setLayout(new BoxLayout(passBlock, BoxLayout.Y_AXIS));
        passBlock.setOpaque(false);
        passBlock.setAlignmentX(Component.CENTER_ALIGNMENT);
        passBlock.setMaximumSize(new Dimension(380, 90));

        JLabel passLabel = UIHelper.createFieldLabel("Password");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBackground(UIHelper.INPUT_BG);
        passwordField.setForeground(UIHelper.TEXT_PRIMARY);
        passwordField.setCaretColor(UIHelper.TEXT_PRIMARY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(65, 85, 115), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        passBlock.add(passLabel);
        passBlock.add(Box.createVerticalStrut(6));
        passBlock.add(passwordField);

        JButton loginBtn = createLargeAccentButton("Login");
        loginBtn.setBackground(Color.GREEN);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());

        JButton backBtn = createLargeGhostButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> rootLayout.show(rootPanel, "WELCOME"));

        card.add(Box.createVerticalGlue());
        card.add(heading);
        card.add(Box.createVerticalStrut(10));
        card.add(info);
        card.add(Box.createVerticalStrut(26));
        card.add(userBlock);
        card.add(Box.createVerticalStrut(14));
        card.add(passBlock);
        card.add(Box.createVerticalStrut(24));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(backBtn);
        card.add(Box.createVerticalGlue());

        panel.add(card);
        return panel;
    }

    private void loginAs(String role) {
        currentRole = role;
        rebuildAppForRole();
        rootLayout.show(rootPanel, "APP");
        showPage("DASHBOARD");
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        passwordField.setText("");

        if (userService == null) {
            JOptionPane.showMessageDialog(this, "User service not initialized.");
            return;
        }

        String role = userService.getRole(username);
        if (role == null || !userService.isValidLogin(username, password, role)) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            return;
        }

        currentRole = role.toUpperCase();
        loginAs(currentRole);
    }

    private void initializeAccounts() {
        adminAccounts.clear();
        staffAccounts.clear();

        adminAccounts.put("admin", "admin123");

        staffAccounts.put("emp01", "park101");
        staffAccounts.put("emp02", "park102");
        staffAccounts.put("emp03", "park103");
        staffAccounts.put("emp04", "park104");
        staffAccounts.put("emp05", "park105");
        staffAccounts.put("emp06", "park106");
        staffAccounts.put("emp07", "park107");
        staffAccounts.put("emp08", "park108");
    }

    private JPanel createAppShell() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(UIHelper.APP_BG);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(10, 16, 28));
        topBar.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel brand = new JLabel("ParkSmart Control Panel");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel roleLabel = new JLabel("Role-based access active");
        roleLabel.setForeground(new Color(140, 225, 255));
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        roleLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
        roleLabel.setBackground(new Color(20, 34, 54));
        roleLabel.setOpaque(true);

        JPanel titleBlock = UIHelper.createTransparentPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(brand);
        titleBlock.add(roleLabel);

        JButton logoutButton = UIHelper.createButton("Logout");
        logoutButton.setBackground(Color.RED.darker());
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            currentRole = "";
            rootLayout.show(rootPanel, "LOGIN");
        });

        topBar.add(titleBlock, BorderLayout.WEST);
        topBar.add(logoutButton, BorderLayout.EAST);

        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(230, 0));

        contentHost = new JPanel(new BorderLayout());
        contentHost.setBackground(UIHelper.APP_BG);

        shell.add(topBar, BorderLayout.NORTH);
        shell.add(sidebarPanel, BorderLayout.WEST);
        shell.add(contentHost, BorderLayout.CENTER);

        return shell;
    }

    private void rebuildAppForRole() {
        rebuildSidebar();
        rebuildContent();
        appShell.revalidate();
        appShell.repaint();
    }

    private void rebuildSidebar() {
        sidebarPanel.removeAll();
        sidebarPanel.setBackground(new Color(15, 24, 38));
        sidebarPanel.setBorder(new EmptyBorder(20, 14, 20, 14));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        JLabel navTitle = new JLabel("Navigation");
        navTitle.setForeground(Color.WHITE);
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleText = new JLabel(currentRole.equals("ADMIN") ? "Admin Access" : "Staff Access");
        roleText.setForeground(new Color(120, 220, 255));
        roleText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleText.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleText.setBorder(new EmptyBorder(0, 4, 0, 0));
        roleText.setBackground(new Color(20, 34, 54));
        roleText.setOpaque(true);
        roleText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        roleText.setHorizontalAlignment(SwingConstants.CENTER);

        sidebarPanel.add(navTitle);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(roleText);
        sidebarPanel.add(Box.createVerticalStrut(18));

        sidebarPanel.add(createNavButton("Dashboard", "DASHBOARD"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(createNavButton("Slots", "SLOTS"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(createNavButton("Vehicles", "VEHICLES"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(createNavButton("Reservations", "RESERVATIONS"));
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(createNavButton("Routes", "ROUTES"));
        sidebarPanel.add(Box.createVerticalStrut(10));

        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            sidebarPanel.add(createNavButton("Analytics", "ANALYTICS"));
            sidebarPanel.add(Box.createVerticalStrut(10));
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void rebuildContent() {
        contentHost.removeAll();

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(UIHelper.APP_BG);

        dashboardPanel = new DashboardPanel(
                currentRole,
                slotService,
                vehicleService,
                reservationService,
                analyticsService
        );

        slotPanel = new SlotPanel(slotService, this::refreshAllPanels);
        vehiclePanel = new VehiclePanel(vehicleService, slotService, this::refreshAllPanels);
        reservationPanel = new ReservationPanel(reservationService, this::refreshAllPanels);
        routePanel = new RoutePanel(routeService);
        analyticsPanel = new AnalyticsPanel(analyticsService);
        userManagementPanel = new UserManagementPanel(userService, this::refreshAllPanels);

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(slotPanel, "SLOTS");
        contentPanel.add(vehiclePanel, "VEHICLES");
        contentPanel.add(reservationPanel, "RESERVATIONS");
        contentPanel.add(routePanel, "ROUTES");

        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            contentPanel.add(analyticsPanel, "ANALYTICS");
            contentPanel.add(userManagementPanel, "USERS");
        }

        contentPanel.add(createPlaceholderPage(), "HOME");

        contentHost.add(contentPanel, BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
    }

    private void refreshAllPanels() {
        if (dashboardPanel != null) {
            dashboardPanel.refreshDashboard();
        }
        if (slotPanel != null) {
            slotPanel.refreshTable();
        }
        if (vehiclePanel != null) {
            vehiclePanel.refreshTable();
        }
        if (reservationPanel != null) {
            reservationPanel.refreshTable();
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createPlaceholderPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.APP_BG);

        JLabel text = new JLabel("Select a page from the sidebar");
        text.setForeground(new Color(220, 230, 245));
        text.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(text);

        return panel;
    }

    private JButton createNavButton(String text, String page) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(UIHelper.BUTTON_BG);
        btn.setForeground(UIHelper.TEXT_PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 72, 102), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> showPage(page));
        return btn;
    }

    private void showPage(String page) {
        if (contentLayout != null && contentPanel != null) {
            contentLayout.show(contentPanel, page);
        }
    }

    private JButton createLargeAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(UIHelper.ACCENT_BG);
        button.setForeground(UIHelper.TEXT_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(220, 44));
        return button;
    }

    private JButton createLargeGhostButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(UIHelper.BUTTON_BG);
        button.setForeground(UIHelper.TEXT_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setPreferredSize(new Dimension(160, 42));
        return button;
    }
}