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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnalyticsPanel extends JPanel {
    private final AnalyticsService analyticsService;
    private final VehicleService vehicleService;
    private final SlotService slotService;
    private final ReservationService reservationService;

    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private JTextArea insightArea;
    private JPanel metricGrid;

    public AnalyticsPanel(AnalyticsService analyticsService,
                          VehicleService vehicleService,
                          SlotService slotService,
                          ReservationService reservationService) {
        this.analyticsService = analyticsService;
        this.vehicleService = vehicleService;
        this.slotService = slotService;
        this.reservationService = reservationService;

        setLayout(new BorderLayout(18, 18));
        setBackground(UIHelper.APP_BG);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = UIHelper.createTransparentPanel();
        header.setLayout(new BorderLayout());

        JPanel titleBox = UIHelper.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitleLabel("Analytics Center");
        JLabel subtitle = UIHelper.createSubtitleLabel("Live occupancy, revenue estimate, reservation flow, and block performance");

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JPanel actions = UIHelper.createTransparentPanel();
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton refreshBtn = UIHelper.createButton("Refresh Records");
        refreshBtn.addActionListener(e -> refreshAnalytics());

        JButton optimizeBtn = UIHelper.createButton("Run Optimization");
        optimizeBtn.addActionListener(e -> runOptimization());

        actions.add(refreshBtn);
        actions.add(optimizeBtn);

        header.add(titleBox, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        return header;
    }

    private JPanel createBody() {
        JPanel body = UIHelper.createTransparentPanel();
        body.setLayout(new BorderLayout(18, 18));

        metricGrid = createMetricGrid();
        body.add(metricGrid, BorderLayout.NORTH);
        body.add(createCenterGrid(), BorderLayout.CENTER);

        refreshAnalytics();
        return body;
    }

    private JPanel createMetricGrid() {
        JPanel grid = UIHelper.createTransparentPanel();
        grid.setLayout(new GridLayout(1, 4, 16, 16));
        return grid;
    }

    private JPanel createCenterGrid() {
        JPanel grid = UIHelper.createTransparentPanel();
        grid.setLayout(new GridLayout(1, 2, 18, 18));

        grid.add(createRankingCard());
        grid.add(createInsightCard());

        return grid;
    }

    private JPanel createRankingCard() {
        JPanel card = createSectionCard("Block Ranking");

        String[] columns = {"Rank", "Block", "Occupancy", "Approx Revenue"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankingTable = new JTable(tableModel);
        UIHelper.styleTable(rankingTable);

        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane scrollPane = UIHelper.wrapScroll(rankingTable, UIHelper.PANEL_BG);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInsightCard() {
        JPanel card = createSectionCard("Operational Insights");

        insightArea = UIHelper.createTextArea();
        JScrollPane scrollPane = UIHelper.wrapScroll(insightArea, UIHelper.CARD_BG);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSectionCard(String title) {
        JPanel card = UIHelper.createCardPanel();

        JLabel titleLabel = UIHelper.createSectionLabel(title);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }

    public void refreshAnalytics() {
        loadAnalyticsData();
        refreshMetricGrid();
        revalidate();
        repaint();
    }

    private void refreshMetricGrid() {
        if (metricGrid == null) {
            return;
        }

        metricGrid.removeAll();

        int totalVehicles = vehicleService.getAllVehicles().size();
        int approxProfit = calculateApproxProfit();
        int currentOccupancy = calculateCurrentOccupancyPercent();
        int confirmedReservations = reservationService.getConfirmedReservationCount();

        metricGrid.add(UIHelper.createStatCard(
                "Vehicles Tracked",
                String.valueOf(totalVehicles),
                "Vehicle records currently available"
        ));
        metricGrid.add(UIHelper.createStatCard(
                "Approx Profit",
                "₹" + approxProfit,
                "Estimated from live parking and reservation data"
        ));
        metricGrid.add(UIHelper.createStatCard(
                "Current Occupancy",
                currentOccupancy + "%",
                "Live occupied-slot percentage"
        ));
        metricGrid.add(UIHelper.createStatCard(
                "Confirmed Reservations",
                String.valueOf(confirmedReservations),
                "Current confirmed reservation count"
        ));

        metricGrid.revalidate();
        metricGrid.repaint();
    }

    private void loadAnalyticsData() {
        if (tableModel == null || insightArea == null) {
            return;
        }

        tableModel.setRowCount(0);

        List<BlockMetric> blockMetrics = buildBlockMetrics();
        blockMetrics.sort(Comparator
                .comparingInt(BlockMetric::getOccupancyPercent).reversed()
                .thenComparingInt(BlockMetric::getRevenue).reversed());

        for (int i = 0; i < blockMetrics.size(); i++) {
            BlockMetric metric = blockMetrics.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    metric.getBlockName(),
                    metric.getOccupancyPercent() + "%",
                    "₹" + metric.getRevenue()
            });
        }

        int activeCars = countActiveVehiclesByType("Car");
        int activeBikes = countActiveVehiclesByType("Bike");
        int activeEVs = countActiveVehiclesByType("EV");
        int chargingNow = countVehiclesByStatus("Charging");
        int parkedNow = countVehiclesByStatus("Parked");
        int emptySlots = slotService.getAvailableSlotCount();
        int confirmedReservations = reservationService.getConfirmedReservationCount();
        int pendingReservations = reservationService.getPendingReservationCount();
        int cancelledReservations = reservationService.getCancelledReservationCount();

        BlockMetric busiestBlock = blockMetrics.isEmpty() ? null : blockMetrics.get(0);

        insightArea.setText(
                "• Analytics is now calculated from live slot, vehicle, and reservation services.\n\n" +
                "• Busiest block: " + (busiestBlock != null ? busiestBlock.getBlockName() : "-") +
                " with " + (busiestBlock != null ? busiestBlock.getOccupancyPercent() : 0) + "% occupancy.\n\n" +
                "• Active vehicles by type -> Cars: " + activeCars +
                ", Bikes: " + activeBikes +
                ", EVs: " + activeEVs + ".\n\n" +
                "• Vehicles currently parked: " + parkedNow +
                " | Vehicles charging: " + chargingNow + ".\n\n" +
                "• Empty slots available across all blocks: " + emptySlots + ".\n\n" +
                "• Reservation summary -> Confirmed: " + confirmedReservations +
                ", Pending: " + pendingReservations +
                ", Cancelled: " + cancelledReservations + ".\n\n" +
                "• Approx profit estimate: ₹" + calculateApproxProfit() + ".\n\n" +
                "• Optimization records available: " + analyticsService.getAnalyticsRecords().size()
        );
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

    private int calculateCurrentOccupancyPercent() {
        int totalSlots = slotService.getAllSlots().size();
        if (totalSlots == 0) {
            return 0;
        }

        int occupied = totalSlots - slotService.getAvailableSlotCount();
        return (int) Math.round((occupied * 100.0) / totalSlots);
    }

    private List<BlockMetric> buildBlockMetrics() {
        String[] blocks = {"ALPHA", "BETA", "GAMMA", "DELTA", "EPSILON"};
        List<BlockMetric> metrics = new ArrayList<>();

        for (String block : blocks) {
            int totalSlots = 0;
            int occupiedSlots = 0;
            int revenue = 0;

            for (ParkingSlot slot : slotService.getAllSlots()) {
                if (slot.getBlockName().equalsIgnoreCase(block)) {
                    totalSlots++;

                    if (!slot.isAvailable()) {
                        occupiedSlots++;

                        if ("EV".equalsIgnoreCase(slot.getSlotGroup())) {
                            revenue += 180;
                        } else if ("B".equalsIgnoreCase(slot.getSlotGroup())) {
                            revenue += 70;
                        } else {
                            revenue += 100;
                        }
                    }
                }
            }

            for (Reservation reservation : reservationService.getAllReservations()) {
                if (reservation.getSlotId() != null
                        && reservation.getSlotId().toUpperCase().startsWith(block + "-")
                        && "Confirmed".equalsIgnoreCase(reservation.getStatus())) {
                    revenue += 60;
                }
            }

            int occupancyPercent = totalSlots == 0 ? 0
                    : (int) Math.round((occupiedSlots * 100.0) / totalSlots);

            metrics.add(new BlockMetric(block, occupiedSlots, occupancyPercent, revenue));
        }

        return metrics;
    }

    private int countActiveVehiclesByType(String type) {
        int count = 0;

        for (Vehicle vehicle : vehicleService.getAllVehicles()) {
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

    private void runOptimization() {
        analyticsService.runOptimization();
        refreshAnalytics();

        insightArea.append("\n\n[Optimization] Greedy allocation and dynamic programming analysis completed with refreshed live metrics.");

        JOptionPane.showMessageDialog(
                this,
                "Optimization algorithms executed successfully!",
                "Analytics Updated",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static class BlockMetric {
        private final String blockName;
        
        private final int occupancyPercent;
        private final int revenue;

        public BlockMetric(String blockName, int occupiedSlots, int occupancyPercent, int revenue) {
            this.blockName = blockName;
            this.occupancyPercent = occupancyPercent;
            this.revenue = revenue;
        }

        public String getBlockName() {
            return blockName;
        }

        

        public int getOccupancyPercent() {
            return occupancyPercent;
        }

        public int getRevenue() {
            return revenue;
        }
    }
}