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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SlotPanel extends JPanel {
    private final SlotService slotService;
    private final VehicleService vehicleService;
    private final ReservationService reservationService;
    private final Runnable onDataChanged;

    private JTable slotTable;
    private DefaultTableModel tableModel;
    private JLabel totalSlotsLabel;
    private JLabel availableSlotsLabel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    private String selectedBlock = "ALL";
    private String selectedGroup = "ALL";

    public SlotPanel(SlotService slotService,
                     VehicleService vehicleService,
                     ReservationService reservationService,
                     Runnable onDataChanged) {
        this.slotService = slotService;
        this.vehicleService = vehicleService;
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
        JPanel wrapper = UIHelper.createTransparentPanel();
        wrapper.setLayout(new BorderLayout(0, 14));

        JPanel topRow = UIHelper.createTransparentPanel();
        topRow.setLayout(new BorderLayout(12, 12));

        JPanel titleBox = UIHelper.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitleLabel("Slot Management");
        JLabel subtitle = UIHelper.createSubtitleLabel("Block-wise parking activity, A/B/EV slot filters, and slot history inspection");

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
            selectedGroup = "ALL";
            statusFilter.setSelectedItem("Filled");
            refreshTable();
        });

        JButton alphaBtn = UIHelper.createButton("ALPHA");
        alphaBtn.addActionListener(e -> {
            selectedBlock = "ALPHA";
            selectedGroup = "ALL";
            refreshTable();
        });

        JButton betaBtn = UIHelper.createButton("BETA");
        betaBtn.addActionListener(e -> {
            selectedBlock = "BETA";
            selectedGroup = "ALL";
            refreshTable();
        });

        JButton gammaBtn = UIHelper.createButton("GAMMA");
        gammaBtn.addActionListener(e -> {
            selectedBlock = "GAMMA";
            selectedGroup = "ALL";
            refreshTable();
        });

        JButton deltaBtn = UIHelper.createButton("DELTA");
        deltaBtn.addActionListener(e -> {
            selectedBlock = "DELTA";
            selectedGroup = "ALL";
            refreshTable();
        });

        JButton epsilonBtn = UIHelper.createButton("EPSILON");
        epsilonBtn.addActionListener(e -> {
            selectedBlock = "EPSILON";
            selectedGroup = "ALL";
            refreshTable();
        });

        blockBar.add(allBtn);
        blockBar.add(alphaBtn);
        blockBar.add(betaBtn);
        blockBar.add(gammaBtn);
        blockBar.add(deltaBtn);
        blockBar.add(epsilonBtn);

        JPanel groupBar = UIHelper.createTransparentPanel();
        groupBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton allGroupBtn = UIHelper.createButton("ALL GROUPS");
        allGroupBtn.addActionListener(e -> {
            selectedGroup = "ALL";
            refreshTable();
        });

        JButton aBtn = UIHelper.createButton("A");
        aBtn.addActionListener(e -> {
            selectedGroup = "A";
            refreshTable();
        });

        JButton bBtn = UIHelper.createButton("B");
        bBtn.addActionListener(e -> {
            selectedGroup = "B";
            refreshTable();
        });

        JButton evBtn = UIHelper.createButton("EV");
        evBtn.addActionListener(e -> {
            selectedGroup = "EV";
            refreshTable();
        });

        groupBar.add(allGroupBtn);
        groupBar.add(aBtn);
        groupBar.add(bBtn);
        groupBar.add(evBtn);

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
            selectedGroup = "ALL";
            refreshTable();
        });

        filterRow.add(UIHelper.createFieldLabel("Search"));
        filterRow.add(searchField);
        filterRow.add(UIHelper.createFieldLabel("Status"));
        filterRow.add(statusFilter);
        filterRow.add(clearBtn);

        JPanel combinedCenter = UIHelper.createTransparentPanel();
        combinedCenter.setLayout(new BoxLayout(combinedCenter, BoxLayout.Y_AXIS));
        combinedCenter.add(blockBar);
        combinedCenter.add(Box.createVerticalStrut(8));
        combinedCenter.add(groupBar);
        combinedCenter.add(Box.createVerticalStrut(8));
        combinedCenter.add(filterRow);

        wrapper.add(topRow, BorderLayout.NORTH);
        wrapper.add(combinedCenter, BorderLayout.CENTER);

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

        String blockName = JOptionPane.showInputDialog(this, "Enter Block Name (ALPHA/BETA/GAMMA/DELTA/EPSILON):");
        if (blockName == null || blockName.trim().isEmpty()) {
            return;
        }

        String slotGroup = JOptionPane.showInputDialog(this, "Enter Slot Group (A/B/EV):");
        if (slotGroup == null || slotGroup.trim().isEmpty()) {
            return;
        }

        String slotNumberValue = JOptionPane.showInputDialog(this, "Enter Slot Number Part (example: 31 or 11):");
        if (slotNumberValue == null || slotNumberValue.trim().isEmpty()) {
            return;
        }

        String slotType = JOptionPane.showInputDialog(this, "Enter Slot Type:");
        if (slotType == null || slotType.trim().isEmpty()) {
            return;
        }

        try {
            int slotId = Integer.parseInt(slotIdInput.trim());
            String upperBlock = blockName.trim().toUpperCase();
            String upperGroup = slotGroup.trim().toUpperCase();
            String slotNumber = upperGroup + slotNumberValue.trim();
            String displaySlotId = upperBlock + "-" + slotNumber;

            for (ParkingSlot slot : slotService.getAllSlots()) {
                if (slot.getSlotId() == slotId) {
                    JOptionPane.showMessageDialog(this, "Slot ID already exists.");
                    return;
                }
                if (slot.getDisplaySlotId().equalsIgnoreCase(displaySlotId)) {
                    JOptionPane.showMessageDialog(this, "Display Slot ID already exists.");
                    return;
                }
            }

            ParkingSlot newSlot = new ParkingSlot(
                    slotId,
                    upperBlock,
                    upperGroup,
                    slotNumber,
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
        String slotDisplayId = String.valueOf(tableModel.getValueAt(row, 0));
        ParkingSlot selectedSlot = slotService.getSlotByDisplayId(slotDisplayId);

        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Slot details not found.");
            return;
        }

        Vehicle currentVehicle = vehicleService.getCurrentVehicleInSlot(slotDisplayId);
        Vehicle lastVehicle = vehicleService.getLastVehicleForSlot(slotDisplayId);
        List<Vehicle> utilizedVehicles = vehicleService.getVehiclesBySlot(slotDisplayId);
        List<Reservation> slotReservations = reservationService.getReservationsBySlot(slotDisplayId);

        String currentVehicleNumber = currentVehicle != null ? currentVehicle.getVehicleNumber() : "-";
        String lastVehicleNumber = lastVehicle != null ? lastVehicle.getVehicleNumber() : "-";
        String reservationSummary = slotReservations.isEmpty() ? "0" : String.valueOf(slotReservations.size());

        Object[] options = {"View Current & Last Info", "View 6-Month History", "Close"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Slot: " + selectedSlot.getDisplaySlotId() +
                        "\nBlock: " + selectedSlot.getBlockName() +
                        "\nGroup: " + selectedSlot.getSlotGroup() +
                        "\nType: " + selectedSlot.getSlotType() +
                        "\nStatus: " + selectedSlot.getStatus() +
                        "\nCurrent Vehicle: " + currentVehicleNumber +
                        "\nLast Vehicle: " + lastVehicleNumber +
                        "\nReservations on Slot: " + reservationSummary +
                        "\nLast Entry: " + selectedSlot.getLastEntryTime() +
                        "\nLast Exit: " + selectedSlot.getLastExitTime(),
                "Slot Details",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[2]
        );

        if (choice == 0) {
            showCurrentAndLastInfo(selectedSlot, currentVehicle, lastVehicle, slotReservations);
        } else if (choice == 1) {
            showSixMonthHistory(selectedSlot, utilizedVehicles, slotReservations);
        }
    }

    private void showCurrentAndLastInfo(ParkingSlot selectedSlot,
                                        Vehicle currentVehicle,
                                        Vehicle lastVehicle,
                                        List<Reservation> slotReservations) {
        StringBuilder reservationText = new StringBuilder();

        if (slotReservations == null || slotReservations.isEmpty()) {
            reservationText.append("- No reservations found for this slot.\n");
        } else {
            for (Reservation reservation : slotReservations) {
                reservationText.append("- ")
                        .append(reservation.getReservationId())
                        .append(" | Vehicle: ")
                        .append(reservation.getVehicleNumber())
                        .append(" | Time: ")
                        .append(reservation.getReservationTime())
                        .append(" | Status: ")
                        .append(reservation.getStatus())
                        .append("\n");
            }
        }

        JTextArea textArea = new JTextArea(
                "Slot: " + selectedSlot.getDisplaySlotId() +
                        "\nBlock: " + selectedSlot.getBlockName() +
                        "\nGroup: " + selectedSlot.getSlotGroup() +
                        "\nType: " + selectedSlot.getSlotType() +
                        "\nStatus: " + selectedSlot.getStatus() +
                        "\n\nCurrent Vehicle:\n" +
                        (currentVehicle == null ? "- No active vehicle in this slot.\n" :
                                "Vehicle Number: " + currentVehicle.getVehicleNumber() +
                                        "\nOwner: " + currentVehicle.getOwnerName() +
                                        "\nType: " + currentVehicle.getVehicleType() +
                                        "\nEntry Time: " + currentVehicle.getEntryTime() +
                                        "\nStatus: " + currentVehicle.getStatus() + "\n") +
                        "\nLast Vehicle:\n" +
                        (lastVehicle == null ? "- No previous vehicle found.\n" :
                                "Vehicle Number: " + lastVehicle.getVehicleNumber() +
                                        "\nOwner: " + lastVehicle.getOwnerName() +
                                        "\nType: " + lastVehicle.getVehicleType() +
                                        "\nEntry Time: " + lastVehicle.getEntryTime() +
                                        "\nExit Time: " + lastVehicle.getExitTime() +
                                        "\nStatus: " + lastVehicle.getStatus() + "\n") +
                        "\nReservations for this Slot:\n" + reservationText
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
                "Current and Last Slot Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showSixMonthHistory(ParkingSlot selectedSlot,
                                     List<Vehicle> utilizedVehicles,
                                     List<Reservation> slotReservations) {
        StringBuilder historyText = new StringBuilder();
        historyText.append("6-Month Utilization History for ").append(selectedSlot.getDisplaySlotId()).append("\n\n");
        historyText.append("Block: ").append(selectedSlot.getBlockName()).append("\n");
        historyText.append("Group: ").append(selectedSlot.getSlotGroup()).append("\n");
        historyText.append("Type: ").append(selectedSlot.getSlotType()).append("\n\n");

        Vehicle lastVehicle = vehicleService.getLastVehicleForSlot(selectedSlot.getDisplaySlotId());
        historyText.append("Last Visited Vehicle: ")
                .append(lastVehicle != null ? lastVehicle.getVehicleNumber() : "-")
                .append("\n\n");

        historyText.append("Past Vehicles Parked on this Slot:\n");
        if (utilizedVehicles == null || utilizedVehicles.isEmpty()) {
            historyText.append("- No vehicle utilization found.\n");
        } else {
            for (Vehicle vehicle : utilizedVehicles) {
                historyText.append("- ")
                        .append(vehicle.getVehicleNumber())
                        .append(" | ")
                        .append(vehicle.getOwnerName())
                        .append(" | ")
                        .append(vehicle.getVehicleType())
                        .append(" | Status: ")
                        .append(vehicle.getStatus())
                        .append(" | Entry: ")
                        .append(vehicle.getEntryTime())
                        .append(" | Exit: ")
                        .append(vehicle.getExitTime())
                        .append("\n");
            }
        }

        historyText.append("\nReservations Made on this Slot:\n");
        if (slotReservations == null || slotReservations.isEmpty()) {
            historyText.append("- No reservations found.\n");
        } else {
            for (Reservation reservation : slotReservations) {
                historyText.append("- ")
                        .append(reservation.getReservationId())
                        .append(" | Vehicle: ")
                        .append(reservation.getVehicleNumber())
                        .append(" | Time: ")
                        .append(reservation.getReservationTime())
                        .append(" | Status: ")
                        .append(reservation.getStatus())
                        .append("\n");
            }
        }

        historyText.append("\nSlot Event History:\n");
        if (selectedSlot.getHistory() != null && !selectedSlot.getHistory().isEmpty()) {
            for (String event : selectedSlot.getHistory()) {
                historyText.append("- ").append(event).append("\n");
            }
        } else {
            historyText.append("- No slot event history found.\n");
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
                "6-Month Slot History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        List<ParkingSlot> filtered = new ArrayList<>();
        String searchText = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedStatus = statusFilter == null ? "Filled" : String.valueOf(statusFilter.getSelectedItem());

        for (ParkingSlot slot : slotService.getAllSlots()) {
            boolean blockMatch = selectedBlock.equals("ALL") || slot.getBlockName().equalsIgnoreCase(selectedBlock);
            boolean groupMatch = selectedGroup.equals("ALL") || slot.getSlotGroup().equalsIgnoreCase(selectedGroup);

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

            if (blockMatch && groupMatch && statusMatch && searchMatch) {
                filtered.add(slot);
            }
        }

        filtered.sort(Comparator.comparing(ParkingSlot::getLastEntryTime).reversed());

        for (ParkingSlot slot : filtered) {
            Vehicle currentVehicle = vehicleService.getCurrentVehicleInSlot(slot.getDisplaySlotId());
            String vehicle = currentVehicle != null ? currentVehicle.getVehicleNumber() : "-";

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