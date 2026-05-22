package gui;

import service.AnalyticsService;
import service.ReservationService;
import service.SlotService;
import service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final String role;
    private final SlotService slotService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    

    private JPanel statsGrid;

    public DashboardPanel(String role,
                          SlotService slotService,
                          VehicleService vehicleService,
                          ReservationService reservationService,
                          AnalyticsService analyticsService) {
        this.role = role == null ? "STAFF" : role.toUpperCase();
        this.slotService = slotService;
        this.vehicleService = vehicleService;
        this.reservationService = reservationService;
        

        setLayout(new BorderLayout(18, 18));
        setBackground(UIHelper.APP_BG);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(createTopSection(), BorderLayout.NORTH);
        add(createMainSection(), BorderLayout.CENTER);
    }

    private JPanel createTopSection() {
        JPanel top = UIHelper.createTransparentPanel();
        top.setLayout(new BorderLayout());

        JPanel titleBox = UIHelper.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitleLabel(getTitleText());
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JLabel subtitle = UIHelper.createSubtitleLabel(getSubtitleText());

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(subtitle);

        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        badge.setBackground(new Color(20, 34, 54));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(52, 82, 120), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        JLabel badgeText = new JLabel(role.equals("ADMIN") ? "ADMIN VIEW" : "STAFF VIEW");
        badgeText.setForeground(new Color(120, 225, 255));
        badgeText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badgeText.setBorder(getBorder());
        badge.add(badgeText);

        top.add(titleBox, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);
        return top;
    }

    private JPanel createMainSection() {
        JPanel main = UIHelper.createTransparentPanel();
        main.setLayout(new BorderLayout(18, 18));

        statsGrid = createStatsGrid();
        main.add(statsGrid, BorderLayout.NORTH);
        main.add(createBottomGrid(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createStatsGrid() {
        JPanel grid = UIHelper.createTransparentPanel();
        grid.setLayout(new GridLayout(1, 4, 16, 16));
    
        int activeStaffNow = "ADMIN".equals(role) ? 8 : 5;
        int evCharging = slotService.getAllSlots().size() > 0 ? Math.min(2, slotService.getAllSlots().size()) : 0;
        int parkedVehicles = vehicleService.getAllVehicles().size();
        int confirmedReservations = reservationService.getAllReservations().size();
    
        if (role.equals("ADMIN")) {
            grid.add(UIHelper.createStatCard("Current Staff", String.valueOf(activeStaffNow), "Employees working right now"));
            grid.add(UIHelper.createStatCard("EV Charging", String.valueOf(evCharging), "Vehicles charging currently"));
            grid.add(UIHelper.createStatCard("Parked Vehicles", String.valueOf(parkedVehicles), "Vehicles parked inside"));
            grid.add(UIHelper.createStatCard("Confirmed Reservations", String.valueOf(confirmedReservations), "Reservations confirmed in system"));
        } else {
            grid.add(UIHelper.createStatCard("EV Charging", String.valueOf(evCharging), "Vehicles charging currently"));
            grid.add(UIHelper.createStatCard("Parked Vehicles", String.valueOf(parkedVehicles), "Vehicles parked inside"));
            grid.add(UIHelper.createStatCard("Confirmed Reservations", String.valueOf(confirmedReservations), "Reservations confirmed in system"));
            grid.add(UIHelper.createStatCard("Route Guidance", "ON", "Use route panel for nearest block guidance"));
        }
    
        return grid;
    }

    private JPanel createBottomGrid() {
        JPanel grid = UIHelper.createTransparentPanel();
        grid.setLayout(new GridLayout(1, 2, 18, 18));
    
        grid.add(createActivityCard());
        grid.add(role.equals("ADMIN") ? createAdminInsightCard() : createStaffActionCard());
    
        return grid;
    }

    private JPanel createActivityCard() {
        JPanel card = createSectionCard("System Activity");
    
        JTextArea area = UIHelper.createTextArea();
        area.setText(getActivityText());
    
        card.add(area, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAdminInsightCard() {
        JPanel card = createSectionCard("Admin Insights");
    
        JTextArea area = UIHelper.createTextArea();
    
        int vehiclesVisitedToday = vehicleService.getAllVehicles().size() + reservationService.getAllReservations().size();
        int activeStaffNow = "ADMIN".equals(role) ? 8 : 5;
        int approxProfit = vehicleService.getAllVehicles().size() * 50 + vehiclesVisitedToday * 20;
    
        area.setText(
                "• Total vehicles visited today: " + vehiclesVisitedToday + "\n\n" +
                "• Active staff now: " + activeStaffNow + "\n\n" +
                "• Approx profit: ₹" + approxProfit
        );
    
        card.add(area, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createStaffActionCard() {
    JPanel card = createSectionCard("Staff Actions");

    JTextArea area = UIHelper.createTextArea();
    area.setText(
        "• EV charging vehicles: " + Math.min(2, vehicleService.getAllVehicles().size()) + "\n\n" +
        "• Parked vehicles: " + vehicleService.getAllVehicles().size() + "\n\n" +
        "• Confirmed reservations: " + reservationService.getAllReservations().size() + "\n\n" +
        "• Use route panel for nearest block guidance."
        );

    card.add(area, BorderLayout.CENTER);
    return card;
}

    private JPanel createSectionCard(String title) {
        JPanel card = UIHelper.createCardPanel();

        JLabel titleLabel = UIHelper.createSectionLabel(title);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }

    private String getTitleText() {
        return role.equals("ADMIN") ? "Admin Dashboard" : "Staff Dashboard";
    }

    private String getSubtitleText() {
        return role.equals("ADMIN")
                ? "Monitor parking performance, traffic flow, and operational analytics"
                : "Track live parking operations, slot availability, and reservations";
    }

    private String getActivityText() {
        int currentEmployees = "ADMIN".equals(role) ? 8 : 5;
        int evCharging = slotService.getAllSlots().size() > 0 ? Math.min(2, slotService.getAllSlots().size()) : 0;
        int parkedVehicles = vehicleService.getAllVehicles().size();
        int confirmedReservations = reservationService.getAllReservations().size();
    
        if (role.equals("ADMIN")) {
            return "• Current employees working: " + currentEmployees + "\n\n"
                    + "• EV charging vehicles: " + evCharging + "\n\n"
                    + "• Parked vehicles: " + parkedVehicles + "\n\n"
                    + "• Confirmed reservations: " + confirmedReservations;
        }
    
        return "• EV charging vehicles: " + evCharging + "\n\n"
                + "• Parked vehicles: " + parkedVehicles + "\n\n"
                + "• Confirmed reservations: " + confirmedReservations;
    }

    public void refreshDashboard() {
        removeAll();
        add(createTopSection(), BorderLayout.NORTH);
        add(createMainSection(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}