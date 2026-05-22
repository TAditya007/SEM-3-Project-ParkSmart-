package gui;

import model.ParkingSlot;
import model.Vehicle;
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
    private final Runnable onDataChanged;

    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private JLabel totalVehiclesLabel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> dateFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    public VehiclePanel(VehicleService vehicleService, SlotService slotService, Runnable onDataChanged) {
        this.vehicleService = vehicleService;
        this.slotService = slotService;
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
        JLabel subtitle = UIHelper.createSubtitleLabel("Search vehicles, filter records by time, and inspect vehicle history");

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

        JButton refreshBtn = UIHelper.createButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        actions.add(addVehicleBtn);
        actions.add(removeBtn);
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
                showVehicleHistory();
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
        vehicleNumber = vehicleNumber.trim();

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
    newVehicle.addHistory(entryTime + " - Assigned to slot " + slotDisplay + " [" + status + "]");
    vehicleService.addVehicle(newVehicle);

        if (freeSlot != null) {
            freeSlot.assignVehicle(vehicleNumber);
            freeSlot.setLastEntryTime(entryTime);
            freeSlot.setLastExitTime("--");

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
        String exitTime = getCurrentSystemTime();
        int selectedViewRow = vehicleTable.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle row first.");
            return;
        }

        int selectedModelRow = vehicleTable.convertRowIndexToModel(selectedViewRow);
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        if (selectedModelRow < 0 || selectedModelRow >= vehicles.size()) {
            JOptionPane.showMessageDialog(this, "Invalid vehicle selection.");
            return;
        }

        Vehicle selectedVehicle = vehicles.get(selectedModelRow);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Exit vehicle " + selectedVehicle.getVehicleNumber() + " and release its slot?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            releaseSlotByVehicle(selectedVehicle.getVehicleNumber(), exitTime);
            selectedVehicle.setExitTime(exitTime);
            selectedVehicle.setStatus("Exited");
            selectedVehicle.addHistory(exitTime + " - Vehicle exited from slot " + selectedVehicle.getSlotId() + " [Exited]");
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
                break;
            }
        }
    }

    private void showVehicleHistory() {
        int selectedViewRow = vehicleTable.getSelectedRow();
        if (selectedViewRow == -1) {
            return;
        }
    
        int selectedModelRow = vehicleTable.convertRowIndexToModel(selectedViewRow);
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
    
        if (selectedModelRow < 0 || selectedModelRow >= vehicles.size()) {
            return;
        }
    
        Vehicle selectedVehicle = vehicles.get(selectedModelRow);
    
        StringBuilder historyText = new StringBuilder();
        for (String event : selectedVehicle.getHistory()) {
            historyText.append(event).append("\n");
        }
    
        JTextArea textArea = new JTextArea(
                "Vehicle Number: " + selectedVehicle.getVehicleNumber() +
                "\nOwner: " + selectedVehicle.getOwnerName() +
                "\nType: " + selectedVehicle.getVehicleType() +
                "\nCurrent/Last Slot: " + selectedVehicle.getSlotId() +
                "\nEntry Time: " + selectedVehicle.getEntryTime() +
                "\nExit Time: " + selectedVehicle.getExitTime() +
                "\nStatus: " + selectedVehicle.getStatus() +
                "\n\nHistory:\n" + historyText
        );
    
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(UIHelper.PANEL_BG);
        textArea.setForeground(UIHelper.TEXT_PRIMARY);
    
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(520, 320));
    
        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Vehicle History",
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