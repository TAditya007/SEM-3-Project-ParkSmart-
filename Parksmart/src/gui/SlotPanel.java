package gui;

import model.ParkingSlot;
import service.SlotService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SlotPanel extends JPanel {
    private final SlotService slotService;
    private final Runnable onDataChanged;

    private JTable slotTable;
    private DefaultTableModel tableModel;
    private JLabel totalSlotsLabel;
    private JLabel availableSlotsLabel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    private String selectedBlock = "ALL";

    public SlotPanel(SlotService slotService, Runnable onDataChanged) {
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
        JPanel wrapper = UIHelper.createTransparentPanel();
        wrapper.setLayout(new BorderLayout(0, 14));

        JPanel topRow = UIHelper.createTransparentPanel();
        topRow.setLayout(new BorderLayout(12, 12));

        JPanel titleBox = UIHelper.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitleLabel("Slot Management");
        JLabel subtitle = UIHelper.createSubtitleLabel("Block-wise parking activity, slot status filtering, and quick slot inspection");

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JPanel actions = UIHelper.createTransparentPanel();
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton addBtn = UIHelper.createButton("Add Slot");
        addBtn.addActionListener(e -> addSlot());

        JButton refreshBtn = UIHelper.createButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        actions.add(addBtn);
        actions.add(refreshBtn);

        topRow.add(titleBox, BorderLayout.WEST);
        topRow.add(actions, BorderLayout.EAST);

        JPanel blockBar = UIHelper.createTransparentPanel();
        blockBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton allBtn = UIHelper.createButton("All Filled");
        allBtn.addActionListener(e -> {
            selectedBlock = "ALL";
            refreshTable();
        });

        JButton alphaBtn = UIHelper.createButton("ALPHA");
        alphaBtn.addActionListener(e -> {
            selectedBlock = "ALPHA";
            refreshTable();
        });

        JButton betaBtn = UIHelper.createButton("BETA");
        betaBtn.addActionListener(e -> {
            selectedBlock = "BETA";
            refreshTable();
        });

        JButton gammaBtn = UIHelper.createButton("GAMMA");
        gammaBtn.addActionListener(e -> {
            selectedBlock = "GAMMA";
            refreshTable();
        });

        JButton deltaBtn = UIHelper.createButton("DELTA");
        deltaBtn.addActionListener(e -> {
            selectedBlock = "DELTA";
            refreshTable();
        });

        JButton epsilonBtn = UIHelper.createButton("EPSILON");
        epsilonBtn.addActionListener(e -> {
            selectedBlock = "EPSILON";
            refreshTable();
        });

        blockBar.add(allBtn);
        blockBar.add(alphaBtn);
        blockBar.add(betaBtn);
        blockBar.add(gammaBtn);
        blockBar.add(deltaBtn);
        blockBar.add(epsilonBtn);

        JPanel filterRow = UIHelper.createTransparentPanel();
        filterRow.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        searchField = UIHelper.createTextField();
        searchField.setPreferredSize(new Dimension(230, 38));
        searchField.setToolTipText("Search by slot, vehicle, type, or block");
        addSearchListener();

        statusFilter = new JComboBox<>(new String[]{"Filled", "Available", "Occupied", "Charging", "All"});
        statusFilter.setPreferredSize(new Dimension(150, 38));
        statusFilter.setBackground(UIHelper.INPUT_BG);
        statusFilter.setForeground(UIHelper.TEXT_PRIMARY);
        statusFilter.addActionListener(e -> refreshTable());

        JButton clearBtn = UIHelper.createButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedItem("Filled");
            selectedBlock = "ALL";
            refreshTable();
        });

        filterRow.add(UIHelper.createFieldLabel("Search"));
        filterRow.add(searchField);
        filterRow.add(UIHelper.createFieldLabel("Status"));
        filterRow.add(statusFilter);
        filterRow.add(clearBtn);

        wrapper.add(topRow, BorderLayout.NORTH);
        wrapper.add(blockBar, BorderLayout.CENTER);
        wrapper.add(filterRow, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createTableCard() {
        JPanel card = UIHelper.createCardPanel();

        JLabel cardTitle = UIHelper.createSectionLabel("Slot Activity View");
        cardTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        String[] columns = {"Slot", "Block", "Group", "Type", "Status", "Vehicle", "Last Entry", "Last Exit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        slotTable = new JTable(tableModel);
        UIHelper.styleTable(slotTable);
        slotTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(tableModel);
        slotTable.setRowSorter(sorter);

        slotTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && slotTable.getSelectedRow() != -1) {
                showSlotDetails();
            }
        });

        JScrollPane scrollPane = UIHelper.wrapScroll(slotTable, UIHelper.PANEL_BG);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = UIHelper.createTransparentPanel();
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        totalSlotsLabel = new JLabel("Total Slots: 0");
        totalSlotsLabel.setForeground(UIHelper.TEXT_MUTED);
        totalSlotsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        availableSlotsLabel = new JLabel("Available: 0");
        availableSlotsLabel.setForeground(UIHelper.TEXT_MUTED);
        availableSlotsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        footer.add(totalSlotsLabel);
        footer.add(availableSlotsLabel);

        return footer;
    }

    private void addSearchListener() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTable();
            }
        });
    }

    private void addSlot() {
        String slotIdInput = JOptionPane.showInputDialog(this, "Enter Numeric Slot ID:");
        if (slotIdInput == null || slotIdInput.trim().isEmpty()) {
            return;
        }

        String displaySlotId = JOptionPane.showInputDialog(this, "Enter Display Slot ID (example: ALPHA-A51):");
        if (displaySlotId == null || displaySlotId.trim().isEmpty()) {
            return;
        }

        String blockName = JOptionPane.showInputDialog(this, "Enter Block Name (ALPHA/BETA/GAMMA/DELTA/EPSILON):");
        if (blockName == null || blockName.trim().isEmpty()) {
            return;
        }

        String slotGroup = JOptionPane.showInputDialog(this, "Enter Slot Group (A/B/EV):");
        if (slotGroup == null || slotGroup.trim().isEmpty()) {
            return;
        }

        String slotType = JOptionPane.showInputDialog(this, "Enter Slot Type:");
        if (slotType == null || slotType.trim().isEmpty()) {
            return;
        }

        try {
            int slotId = Integer.parseInt(slotIdInput.trim());

            for (ParkingSlot slot : slotService.getAllSlots()) {
                if (slot.getSlotId() == slotId) {
                    JOptionPane.showMessageDialog(this, "Slot ID already exists.");
                    return;
                }
                if (slot.getDisplaySlotId().equalsIgnoreCase(displaySlotId.trim())) {
                    JOptionPane.showMessageDialog(this, "Display Slot ID already exists.");
                    return;
                }
            }

            ParkingSlot newSlot = new ParkingSlot(
                    slotId,
                    displaySlotId.trim().toUpperCase(),
                    blockName.trim().toUpperCase(),
                    slotGroup.trim().toUpperCase(),
                    slotType.trim(),
                    "Available",
                    true
            );

            slotService.addSlot(newSlot);
            refreshTable();

            if (onDataChanged != null) {
                onDataChanged.run();
            }

            JOptionPane.showMessageDialog(this, "Slot added successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numeric Slot ID must be a number.");
        }
    }

    private void showSlotDetails() {
        int selectedViewRow = slotTable.getSelectedRow();
        if (selectedViewRow == -1) {
            return;
        }

        int row = slotTable.convertRowIndexToModel(selectedViewRow);

        String slot = String.valueOf(tableModel.getValueAt(row, 0));
        String block = String.valueOf(tableModel.getValueAt(row, 1));
        String group = String.valueOf(tableModel.getValueAt(row, 2));
        String type = String.valueOf(tableModel.getValueAt(row, 3));
        String status = String.valueOf(tableModel.getValueAt(row, 4));
        String vehicle = String.valueOf(tableModel.getValueAt(row, 5));
        String lastEntry = String.valueOf(tableModel.getValueAt(row, 6));
        String lastExit = String.valueOf(tableModel.getValueAt(row, 7));

        Object[] options = {"Reserve Slot", "View 6-Month History", "Close"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Slot: " + slot +
                        "\nBlock: " + block +
                        "\nGroup: " + group +
                        "\nType: " + type +
                        "\nStatus: " + status +
                        "\nVehicle: " + vehicle +
                        "\nLast Entry: " + lastEntry +
                        "\nLast Exit: " + lastExit,
                "Slot Details",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (choice == 0) {
            JOptionPane.showMessageDialog(this,
                    "Reservation action will be connected next with ReservationService.");
        } else if (choice == 1) {
            JOptionPane.showMessageDialog(this,
                    "Demo History for " + slot + ":\n"
                            + "12-12-2025 09:10 AM - TS09AB1234\n"
                            + "08-01-2026 11:45 AM - TS10CD5678\n"
                            + "14-03-2026 03:15 PM - TS11EF9012\n"
                            + "02-05-2026 08:55 AM - TS12GH3456");
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        List<ParkingSlot> filtered = new ArrayList<>();
        String searchText = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedStatus = statusFilter == null ? "Filled" : String.valueOf(statusFilter.getSelectedItem());

        for (ParkingSlot slot : slotService.getAllSlots()) {
            boolean blockMatch = selectedBlock.equals("ALL") || slot.getBlockName().equalsIgnoreCase(selectedBlock);

            boolean statusMatch;
            if ("Filled".equalsIgnoreCase(selectedStatus)) {
                statusMatch = !slot.isAvailable();
            } else if ("All".equalsIgnoreCase(selectedStatus)) {
                statusMatch = true;
            } else {
                statusMatch = slot.getStatus().equalsIgnoreCase(selectedStatus);
            }

            String combined = (
                    slot.getDisplaySlotId() + " " +
                    slot.getBlockName() + " " +
                    slot.getSlotGroup() + " " +
                    slot.getSlotType() + " " +
                    slot.getStatus() + " " +
                    slot.getOccupiedVehicleNumber()
            ).toLowerCase();

            boolean searchMatch = searchText.isEmpty() || combined.contains(searchText);

            if (blockMatch && statusMatch && searchMatch) {
                filtered.add(slot);
            }
        }

        filtered.sort(Comparator.comparing(ParkingSlot::getLastEntryTime).reversed());

        for (ParkingSlot slot : filtered) {
            String vehicle = slot.getOccupiedVehicleNumber() == null || slot.getOccupiedVehicleNumber().isEmpty()
                    ? "-"
                    : slot.getOccupiedVehicleNumber();

            tableModel.addRow(new Object[]{
                    slot.getDisplaySlotId(),
                    slot.getBlockName(),
                    slot.getSlotGroup(),
                    slot.getSlotType(),
                    slot.getStatus(),
                    vehicle,
                    slot.getLastEntryTime(),
                    slot.getLastExitTime()
            });
        }

        totalSlotsLabel.setText("Total Slots: " + slotService.getAllSlots().size());
        availableSlotsLabel.setText("Available: " + slotService.getAvailableSlotCount());
    }
}