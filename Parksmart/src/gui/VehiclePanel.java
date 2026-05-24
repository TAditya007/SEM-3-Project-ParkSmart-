package gui;

import model.ParkingSlot;
import model.Reservation;
import model.Vehicle;
import service.ReservationService;
import service.SlotService;
import service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VehiclePanel extends JPanel {
    private final VehicleService vehicleService;
    private final SlotService slotService;
    private final ReservationService reservationService;
    private final Runnable onDataChanged;

    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private JLabel totalVehiclesLabel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> dateFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    public VehiclePanel(VehicleService vehicleService,
                        SlotService slotService,
                        ReservationService reservationService,
                        Runnable onDataChanged) {
        this.vehicleService = vehicleService;
        this.slotService = slotService;
        this.reservationService = reservationService;
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

        JLabel title = UIHelper.createTitleLabel("Vehicle Management");
        JLabel subtitle = UIHelper.createSubtitleLabel("Search vehicles, inspect slot usage, reservation links, and 6-month history");

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JPanel rightBox = UIHelper.createTransparentPanel();
        rightBox.setLayout(new BorderLayout(0, 10));

        JPanel filterPanel = UIHelper.createTransparentPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        searchField = UIHelper.createTextField();
        searchField.setPreferredSize(new Dimension(200, 38));
        searchField.setToolTipText("Search by vehicle number, owner, slot, or type");
        addSearchListener();

        statusFilter = new JComboBox<>(new String[]{"All", "Parked", "Charging", "Exited"});
        statusFilter.setPreferredSize(new Dimension(120, 38));
        statusFilter.setBackground(UIHelper.INPUT_BG);
        statusFilter.setForeground(UIHelper.TEXT_PRIMARY);
        statusFilter.addActionListener(e -> applyFilters());

        dateFilter = new JComboBox<>(new String[]{"All Time", "Today", "Last 3 Days", "Last 7 Days"});
        dateFilter.setPreferredSize(new Dimension(140, 38));
        dateFilter.setBackground(UIHelper.INPUT_BG);
        dateFilter.setForeground(UIHelper.TEXT_PRIMARY);
        dateFilter.addActionListener(e -> applyFilters());

        JButton clearBtn = UIHelper.createButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedItem("All");
            dateFilter.setSelectedItem("All Time");
            applyFilters();
        });

        filterPanel.add(UIHelper.createFieldLabel("Search"));
        filterPanel.add(searchField);
        filterPanel.add(UIHelper.createFieldLabel("Status"));
        filterPanel.add(statusFilter);
        filterPanel.add(UIHelper.createFieldLabel("Date"));
        filterPanel.add(dateFilter);
        filterPanel.add(clearBtn);

        JPanel actions = UIHelper.createTransparentPanel();
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton addVehicleBtn = UIHelper.createButton("Add Vehicle");
        addVehicleBtn.addActionListener(e -> addVehicle());

        JButton removeBtn = UIHelper.createButton("Exit Selected");
        removeBtn.addActionListener(e -> removeSelectedVehicle());

        JButton historyBtn = UIHelper.createButton("History");
        historyBtn.addActionListener(e -> searchVehicleHistory());

        JButton refreshBtn = UIHelper.createButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        actions.add(addVehicleBtn);
        actions.add(removeBtn);
        actions.add(historyBtn);
        actions.add(refreshBtn);

        rightBox.add(filterPanel, BorderLayout.NORTH);
        rightBox.add(actions, BorderLayout.SOUTH);

        header.add(titleBox, BorderLayout.WEST);
        header.add(rightBox, BorderLayout.EAST);

        return header;
    }

    private JPanel createTableCard() {
        JPanel card = UIHelper.createCardPanel();

        JLabel cardTitle = UIHelper.createSectionLabel("Vehicle Records");
        cardTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        String[] columns = {"Vehicle Number", "Owner", "Type", "Slot", "Entry Time", "Exit Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleTable = new JTable(tableModel);
        UIHelper.styleTable(vehicleTable);
        vehicleTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(tableModel);
        vehicleTable.setRowSorter(sorter);

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && vehicleTable.getSelectedRow() != -1) {
                showVehicleDetails();
            }
        });

        vehicleTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        vehicleTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        vehicleTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        vehicleTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        vehicleTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        vehicleTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        vehicleTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = UIHelper.wrapScroll(vehicleTable, UIHelper.PANEL_BG);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = UIHelper.createTransparentPanel();
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));

        totalVehiclesLabel = new JLabel("Total Vehicles: 0");
        totalVehiclesLabel.setForeground(UIHelper.TEXT_MUTED);
        totalVehiclesLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        footer.add(totalVehiclesLabel);
        return footer;
    }

    private void addSearchListener() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(searchText)));
        }

        String selectedStatus = String.valueOf(statusFilter.getSelectedItem());
        if (!"All".equalsIgnoreCase(selectedStatus)) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(selectedStatus) + "$", 6));
        }

        String selectedDate = String.valueOf(dateFilter.getSelectedItem());
        if ("Today".equalsIgnoreCase(selectedDate)) {
            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            filters.add(RowFilter.regexFilter(today, 4));
        } else if ("Last 3 Days".equalsIgnoreCase(selectedDate)) {
            filters.add(RowFilter.regexFilter("18-05-2026|17-05-2026|16-05-2026", 4));
        } else if ("Last 7 Days".equalsIgnoreCase(selectedDate)) {
            filters.add(RowFilter.regexFilter("18-05-2026|17-05-2026|16-05-2026|15-05-2026|14-05-2026|13-05-2026|12-05-2026", 4));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void addVehicle() {
        String vehicleNumber = JOptionPane.showInputDialog(this, "Enter Vehicle Number:");
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            return;
        }
        vehicleNumber = vehicleNumber.trim().toUpperCase();

        for (Vehicle vehicle : vehicleService.getAllVehicles()) {
            if (vehicle.getVehicleNumber().equalsIgnoreCase(vehicleNumber)) {
                JOptionPane.showMessageDialog(this, "Vehicle number already exists.");
                return;
            }
        }

        String ownerName = JOptionPane.showInputDialog(this, "Enter Owner Name:");
        if (ownerName == null || ownerName.trim().isEmpty()) {
            return;
        }

        String vehicleType = JOptionPane.showInputDialog(this, "Enter Vehicle Type:");
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            return;
        }

        addVehicleToSystem(vehicleNumber, ownerName.trim(), vehicleType.trim(), getCurrentSystemTime(), true);
        refreshTable();

        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    private void addVehicleToSystem(String vehicleNumber,
                                    String ownerName,
                                    String vehicleType,
                                    String entryTime,
                                    boolean showMessage) {

        ParkingSlot freeSlot = findFirstAvailableSlot();
        String slotDisplay = (freeSlot != null) ? freeSlot.getDisplaySlotId() : "Not Assigned";
        String status = (freeSlot != null && "EV".equalsIgnoreCase(freeSlot.getSlotGroup())) ? "Charging" : "Parked";

        Vehicle newVehicle = new Vehicle(
                vehicleNumber,
                ownerName,
                vehicleType,
                slotDisplay,
                entryTime,
                "--",
                status
        );
        newVehicle.addHistory(entryTime + " - Vehicle registered in system");
        newVehicle.addHistory(entryTime + " - Assigned to slot " + slotDisplay + " [" + status + "]");
        newVehicle.addSlotVisit(slotDisplay);
        vehicleService.addVehicle(newVehicle);

        if (freeSlot != null) {
            freeSlot.assignVehicle(vehicleNumber);
            freeSlot.setLastEntryTime(entryTime);
            freeSlot.setLastExitTime("--");
            freeSlot.addHistory(entryTime + " - Vehicle " + vehicleNumber + " parked in " + freeSlot.getDisplaySlotId());

            if (showMessage) {
                JOptionPane.showMessageDialog(this,
                        "Vehicle added and assigned to Slot " + freeSlot.getDisplaySlotId() + ".");
            }
        } else {
            if (showMessage) {
                JOptionPane.showMessageDialog(this,
                        "Vehicle added, but no slot is currently available.");
            }
        }
    }

    private String getCurrentSystemTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        return LocalDateTime.now().format(formatter);
    }

    private ParkingSlot findFirstAvailableSlot() {
        for (ParkingSlot slot : slotService.getAllSlots()) {
            if (slot.isAvailable()) {
                return slot;
            }
        }
        return null;
    }

    private void removeSelectedVehicle() {
        int selectedViewRow = vehicleTable.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle row first.");
            return;
        }

        int selectedModelRow = vehicleTable.convertRowIndexToModel(selectedViewRow);
        String vehicleNumber = String.valueOf(tableModel.getValueAt(selectedModelRow, 0));
        Vehicle selectedVehicle = vehicleService.getVehicleByNumber(vehicleNumber);

        if (selectedVehicle == null) {
            JOptionPane.showMessageDialog(this, "Vehicle record not found.");
            return;
        }

        if ("Exited".equalsIgnoreCase(selectedVehicle.getStatus())) {
            JOptionPane.showMessageDialog(this, "This vehicle is already marked as exited.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Exit vehicle " + selectedVehicle.getVehicleNumber() + " and release its slot?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String exitTime = getCurrentSystemTime();
            String slotDisplay = selectedVehicle.getSlotId();

            releaseSlotByVehicle(selectedVehicle.getVehicleNumber(), exitTime);
            selectedVehicle.setExitTime(exitTime);
            selectedVehicle.setStatus("Exited");
            selectedVehicle.addHistory(exitTime + " - Vehicle exited from slot " + slotDisplay + " [Exited]");

            refreshTable();

            if (onDataChanged != null) {
                onDataChanged.run();
            }

            JOptionPane.showMessageDialog(this, "Vehicle exited successfully at " + exitTime + ".");
        }
    }

    private void releaseSlotByVehicle(String vehicleNumber, String exitTime) {
        for (ParkingSlot slot : slotService.getAllSlots()) {
            if (!slot.isAvailable() && vehicleNumber.equalsIgnoreCase(slot.getOccupiedVehicleNumber())) {
                slot.releaseSlot();
                slot.setLastExitTime(exitTime);
                slot.addHistory(exitTime + " - Vehicle " + vehicleNumber + " exited from " + slot.getDisplaySlotId());
                break;
            }
        }
    }

    private void showVehicleDetails() {
        int selectedViewRow = vehicleTable.getSelectedRow();
        if (selectedViewRow == -1) {
            return;
        }

        int selectedModelRow = vehicleTable.convertRowIndexToModel(selectedViewRow);
        String vehicleNumber = String.valueOf(tableModel.getValueAt(selectedModelRow, 0));
        Vehicle selectedVehicle = vehicleService.getVehicleByNumber(vehicleNumber);

        if (selectedVehicle == null) {
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsByVehicle(selectedVehicle.getVehicleNumber());

        Object[] options = {"Current & Last Info", "View 6-Month History", "Close"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Vehicle Number: " + selectedVehicle.getVehicleNumber() +
                        "\nOwner: " + selectedVehicle.getOwnerName() +
                        "\nType: " + selectedVehicle.getVehicleType() +
                        "\nCurrent/Last Slot: " + selectedVehicle.getSlotId() +
                        "\nEntry Time: " + selectedVehicle.getEntryTime() +
                        "\nExit Time: " + selectedVehicle.getExitTime() +
                        "\nStatus: " + selectedVehicle.getStatus() +
                        "\nReservations Found: " + reservations.size(),
                "Vehicle Details",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (choice == 0) {
            showCurrentAndLastInfo(selectedVehicle, reservations);
        } else if (choice == 1) {
            showVehicleHistoryDialog(selectedVehicle, reservations);
        }
    }

    private void showCurrentAndLastInfo(Vehicle selectedVehicle, List<Reservation> reservations) {
        List<String> slotHistory = vehicleService.getSlotHistoryForVehicle(selectedVehicle.getVehicleNumber());
        String currentSlot = vehicleService.getCurrentSlotForVehicle(selectedVehicle.getVehicleNumber());
        String lastVisitedSlot = selectedVehicle.getLastVisitedSlot();

        StringBuilder slotText = new StringBuilder();
        if (slotHistory == null || slotHistory.isEmpty()) {
            slotText.append("- No slot history found.\n");
        } else {
            for (String slot : slotHistory) {
                slotText.append("- ").append(slot).append("\n");
            }
        }

        StringBuilder reservationText = new StringBuilder();
        if (reservations == null || reservations.isEmpty()) {
            reservationText.append("- No reservations found for this vehicle.\n");
        } else {
            for (Reservation reservation : reservations) {
                reservationText.append("- ")
                        .append(reservation.getReservationId())
                        .append(" | Slot: ")
                        .append(reservation.getSlotId())
                        .append(" | Time: ")
                        .append(reservation.getReservationTime())
                        .append(" | Status: ")
                        .append(reservation.getStatus())
                        .append("\n");
            }
        }

        JTextArea textArea = new JTextArea(
                "Vehicle Number: " + selectedVehicle.getVehicleNumber() +
                        "\nOwner: " + selectedVehicle.getOwnerName() +
                        "\nType: " + selectedVehicle.getVehicleType() +
                        "\nCurrent Slot: " + currentSlot +
                        "\nLast Visited Slot: " + (lastVisitedSlot != null ? lastVisitedSlot : "-") +
                        "\nEntry Time: " + selectedVehicle.getEntryTime() +
                        "\nExit Time: " + selectedVehicle.getExitTime() +
                        "\nStatus: " + selectedVehicle.getStatus() +
                        "\n\nSlots Used:\n" + slotText +
                        "\nReservations Made:\n" + reservationText
        );

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(UIHelper.PANEL_BG);
        textArea.setForeground(UIHelper.TEXT_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(620, 360));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Current and Last Vehicle Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showVehicleHistoryDialog(Vehicle selectedVehicle, List<Reservation> reservations) {
        StringBuilder historyText = new StringBuilder();
        historyText.append("6-Month Vehicle History for ").append(selectedVehicle.getVehicleNumber()).append("\n\n");
        historyText.append("Owner: ").append(selectedVehicle.getOwnerName()).append("\n");
        historyText.append("Type: ").append(selectedVehicle.getVehicleType()).append("\n");
        historyText.append("Current/Last Slot: ").append(selectedVehicle.getSlotId()).append("\n");
        historyText.append("Entry Time: ").append(selectedVehicle.getEntryTime()).append("\n");
        historyText.append("Exit Time: ").append(selectedVehicle.getExitTime()).append("\n");
        historyText.append("Status: ").append(selectedVehicle.getStatus()).append("\n\n");

        historyText.append("Slots Used:\n");
        List<String> slotHistory = vehicleService.getSlotHistoryForVehicle(selectedVehicle.getVehicleNumber());
        if (slotHistory == null || slotHistory.isEmpty()) {
            historyText.append("- No slot usage history found.\n");
        } else {
            for (String slot : slotHistory) {
                historyText.append("- ").append(slot).append("\n");
            }
        }

        historyText.append("\nReservations Made:\n");
        if (reservations == null || reservations.isEmpty()) {
            historyText.append("- No reservation history found.\n");
        } else {
            for (Reservation reservation : reservations) {
                historyText.append("- ")
                        .append(reservation.getReservationId())
                        .append(" | Slot: ")
                        .append(reservation.getSlotId())
                        .append(" | Time: ")
                        .append(reservation.getReservationTime())
                        .append(" | Status: ")
                        .append(reservation.getStatus())
                        .append("\n");
            }
        }

        historyText.append("\nVehicle Event History:\n");
        List<String> events = vehicleService.getEventHistoryForVehicle(selectedVehicle.getVehicleNumber());
        if (events == null || events.isEmpty()) {
            historyText.append("- No vehicle event history found.\n");
        } else {
            for (String event : events) {
                historyText.append("- ").append(event).append("\n");
            }
        }

        JTextArea textArea = new JTextArea(historyText.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(UIHelper.PANEL_BG);
        textArea.setForeground(UIHelper.TEXT_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 390));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "6-Month Vehicle History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void searchVehicleHistory() {
        String query = JOptionPane.showInputDialog(this, "Enter vehicle number to search in 6-month history:");
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        List<Vehicle> matchedVehicles = vehicleService.searchVehiclesInHistory(query.trim());
        if (matchedVehicles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No vehicle found in 6-month history.");
            return;
        }

        StringBuilder resultText = new StringBuilder();
        resultText.append("Matching Vehicles in 6-Month History:\n\n");

        for (Vehicle vehicle : matchedVehicles) {
            resultText.append("Vehicle: ").append(vehicle.getVehicleNumber())
                    .append(" | Owner: ").append(vehicle.getOwnerName())
                    .append(" | Type: ").append(vehicle.getVehicleType())
                    .append(" | Current/Last Slot: ").append(vehicle.getSlotId())
                    .append(" | Status: ").append(vehicle.getStatus())
                    .append("\n");
        }

        JTextArea textArea = new JTextArea(resultText.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(UIHelper.PANEL_BG);
        textArea.setForeground(UIHelper.TEXT_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(620, 300));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Vehicle History Search",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        for (Vehicle v : vehicles) {
            tableModel.addRow(new Object[]{
                    v.getVehicleNumber(),
                    v.getOwnerName(),
                    v.getVehicleType(),
                    v.getSlotId(),
                    v.getEntryTime(),
                    v.getExitTime(),
                    v.getStatus()
            });
        }

        totalVehiclesLabel.setText("Total Vehicles: " + vehicles.size());
        applyFilters();
        revalidate();
        repaint();
    }
}