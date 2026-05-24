package gui;

import model.ParkingSlot;
import model.Reservation;
import model.Vehicle;
import service.AnalyticsService;
import service.ReservationService;
import service.SlotService;
import service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final String role;
    private final SlotService slotService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;

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
        badge.add(badgeText);

        top.add(titleBox, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);
        return top;
    }

    private JPanel createMainSection() {
        JPanel main = UIHelper.createTransparentPanel();
        main.setLayout(new BorderLayout(18, 18));

        main.add(createStatsGrid(), BorderLayout.NORTH);
        main.add(createBottomGrid(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createStatsGrid() {
        JPanel grid = UIHelper.createTransparentPanel();
        grid.setLayout(new GridLayout(1, 4, 16, 16));

        if ("ADMIN".equalsIgnoreCase(role)) {
            grid.add(UIHelper.createStatCard(
                    "Vehicles Today",
                    String.valueOf(countVehiclesVisitedToday()),
                    "Total number of vehicles visited today"
            ));
            grid.add(UIHelper.createStatCard(
                    "EV Charging",
                    String.valueOf(countVehiclesByStatus("Charging")),
                    "Vehicles currently charging"
            ));
            grid.add(UIHelper.createStatCard(
                    "Parked Now",
                    String.valueOf(countVehiclesByStatus("Parked")),
                    "Vehicles currently parked"
            ));
            grid.add(UIHelper.createStatCard(
                    "Approx Profit",
                    "₹" + calculateApproxProfit(),
                    "Estimated profit from active parking and reservations"
            ));
        } else {
            grid.add(UIHelper.createStatCard(
                    "Cars",
                    String.valueOf(countActiveVehiclesByType("Car")),
                    "Currently active car entries"
            ));
            grid.add(UIHelper.createStatCard(
                    "Bikes",
                    String.valueOf(countActiveVehiclesByType("Bike")),
                    "Currently active bike entries"
            ));
            grid.add(UIHelper.createStatCard(
                    "EVs",
                    String.valueOf(countActiveVehiclesByType("EV")),
                    "Currently active EV entries"
            ));
            grid.add(UIHelper.createStatCard(
                    "Empty Slots",
                    String.valueOf(slotService.getAvailableSlotCount()),
                    "Available slots across all blocks"
            ));
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
        JPanel card = createSectionCard("Block Operations");

        JTextArea area = UIHelper.createTextArea();
        area.setText(getBlockOperationsText());

        card.add(area, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAdminInsightCard() {
        JPanel card = createSectionCard("Admin Insights");

        JTextArea area = UIHelper.createTextArea();
        area.setText(
                "• Active staff now: " + getActiveStaffNow() + "\n\n" +
                "• EV charging now: " + countVehiclesByStatus("Charging") + "\n\n" +
                "• Vehicles parked now: " + countVehiclesByStatus("Parked") + "\n\n" +
                "• Vehicles visited today: " + countVehiclesVisitedToday() + "\n\n" +
                "• Confirmed reservations: " + reservationService.getConfirmedReservationCount() + "\n\n" +
                "• Pending reservations: " + reservationService.getPendingReservationCount() + "\n\n" +
                "• Approx profit: ₹" + calculateApproxProfit()
        );

        card.add(area, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStaffActionCard() {
        JPanel card = createSectionCard("Staff Actions");

        JTextArea area = UIHelper.createTextArea();
        area.setText(
                "• Cars active now: " + countActiveVehiclesByType("Car") + "\n\n" +
                "• Bikes active now: " + countActiveVehiclesByType("Bike") + "\n\n" +
                "• EVs active now: " + countActiveVehiclesByType("EV") + "\n\n" +
                "• Empty slots: " + slotService.getAvailableSlotCount() + "\n\n" +
                "• Use route panel for nearest block guidance.\n\n" +
                "• Use slot, vehicle, and reservation panels to inspect record details."
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
                ? "Monitor live parking activity, reservation flow, and estimated business performance"
                : "Track live parking operations, vehicle mix, and empty slots by block";
    }

    private String getBlockOperationsText() {
        String[] blocks = {"ALPHA", "BETA", "GAMMA", "DELTA", "EPSILON"};
        StringBuilder text = new StringBuilder();

        for (String block : blocks) {
            int cars = 0;
            int bikes = 0;
            int evs = 0;
            int empty = 0;

            for (ParkingSlot slot : slotService.getAllSlots()) {
                if (slot.getBlockName().equalsIgnoreCase(block)) {
                    if (slot.isAvailable()) {
                        empty++;
                    } else {
                        String vehicleNo = slot.getOccupiedVehicleNumber();
                        Vehicle vehicle = vehicleService.getVehicleByNumber(vehicleNo);
                        if (vehicle != null) {
                            if ("Car".equalsIgnoreCase(vehicle.getVehicleType())) {
                                cars++;
                            } else if ("Bike".equalsIgnoreCase(vehicle.getVehicleType())) {
                                bikes++;
                            } else if ("EV".equalsIgnoreCase(vehicle.getVehicleType())) {
                                evs++;
                            }
                        }
                    }
                }
            }

            text.append("• ").append(block)
                    .append(" -> Cars: ").append(cars)
                    .append(", Bikes: ").append(bikes)
                    .append(", EVs: ").append(evs)
                    .append(", Empty Slots: ").append(empty)
                    .append("\n\n");
        }

        return text.toString().trim();
    }

    private int countActiveVehiclesByType(String type) {
        int count = 0;
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        for (Vehicle vehicle : vehicles) {
            boolean active = "Parked".equalsIgnoreCase(vehicle.getStatus())
                    || "Charging".equalsIgnoreCase(vehicle.getStatus());

            if (active && vehicle.getVehicleType() != null
                    && vehicle.getVehicleType().equalsIgnoreCase(type)) {
                count++;
            }
        }

        return count;
    }

    private int countVehiclesByStatus(String status) {
        int count = 0;
        for (Vehicle vehicle : vehicleService.getAllVehicles()) {
            if (vehicle.getStatus() != null && vehicle.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }

    private int countVehiclesVisitedToday() {
        int count = 0;
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        for (Vehicle vehicle : vehicleService.getAllVehicles()) {
            String entryTime = vehicle.getEntryTime();
            if (entryTime != null && entryTime.startsWith(today)) {
                count++;
            }
        }

        return count;
    }

    private int getActiveStaffNow() {
        return 8;
    }

    private int calculateApproxProfit() {
        int total = 0;

        for (Vehicle vehicle : vehicleService.getAllVehicles()) {
            if ("Charging".equalsIgnoreCase(vehicle.getStatus())) {
                total += 180;
            } else if ("Parked".equalsIgnoreCase(vehicle.getStatus())) {
                if ("EV".equalsIgnoreCase(vehicle.getVehicleType())) {
                    total += 150;
                } else if ("Bike".equalsIgnoreCase(vehicle.getVehicleType())) {
                    total += 70;
                } else {
                    total += 100;
                }
            } else if ("Exited".equalsIgnoreCase(vehicle.getStatus())) {
                total += 40;
            }
        }

        for (Reservation reservation : reservationService.getAllReservations()) {
            if ("Confirmed".equalsIgnoreCase(reservation.getStatus())) {
                total += 60;
            } else if ("Pending".equalsIgnoreCase(reservation.getStatus())) {
                total += 20;
            }
        }

        return total;
    }

    public void refreshDashboard() {
        removeAll();
        add(createTopSection(), BorderLayout.NORTH);
        add(createMainSection(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}