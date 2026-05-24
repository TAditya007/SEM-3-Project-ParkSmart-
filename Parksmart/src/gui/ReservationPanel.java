package gui;

import model.Reservation;
import model.Vehicle;
import model.ParkingSlot;
import service.ReservationService;
import service.VehicleService;
import service.SlotService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReservationPanel extends JPanel {
    private final ReservationService reservationService;
    private final VehicleService vehicleService;
    private final SlotService slotService;
    private final Runnable onDataChanged;

    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JLabel totalReservationsLabel;
    private JLabel confirmedReservationsLabel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private TableRowSorter<DefaultTableModel> sorter;

    public ReservationPanel(ReservationService reservationService,
                            VehicleService vehicleService,
                            SlotService slotService,
                            Runnable onDataChanged) {
        this.reservationService = reservationService;
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
        header.setLayout(new BorderLayout(12, 12));

        JPanel titleBox = UIHelper.createTransparentPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitleLabel("Reservation Management");
        JLabel subtitle = UIHelper.createSubtitleLabel("Handle booking flow, search reservations, and inspect linked slot and vehicle details");

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JPanel rightBox = UIHelper.createTransparentPanel();
        rightBox.setLayout(new BorderLayout(0, 10));

        JPanel filterPanel = UIHelper.createTransparentPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        searchField = UIHelper.createTextField();
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setToolTipText("Search by reservation ID, vehicle number, slot ID, or time");
        addSearchListener();

        statusFilter = new JComboBox<>(new String[]{"All", "Confirmed", "Pending", "Cancelled"});
        statusFilter.setPreferredSize(new Dimension(130, 38));
        statusFilter.setBackground(UIHelper.INPUT_BG);
        statusFilter.setForeground(UIHelper.TEXT_PRIMARY);
        statusFilter.addActionListener(e -> applyFilters());

        JButton clearBtn = UIHelper.createButton("Clear");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            applyFilters();
            reservationTable.clearSelection();
        });

        filterPanel.add(UIHelper.createFieldLabel("Search"));
        filterPanel.add(searchField);
        filterPanel.add(UIHelper.createFieldLabel("Status"));
        filterPanel.add(statusFilter);
        filterPanel.add(clearBtn);

        JPanel actions = UIHelper.createTransparentPanel();
        actions.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton createBtn = UIHelper.createButton("Create Reservation");
        createBtn.addActionListener(e -> createReservation());

        JButton cancelBtn = UIHelper.createButton("Cancel Selected");
        cancelBtn.addActionListener(e -> cancelReservation());

        JButton refreshBtn = UIHelper.createButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());

        actions.add(createBtn);
        actions.add(cancelBtn);
        actions.add(refreshBtn);

        rightBox.add(filterPanel, BorderLayout.NORTH);
        rightBox.add(actions, BorderLayout.SOUTH);

        header.add(titleBox, BorderLayout.WEST);
        header.add(rightBox, BorderLayout.EAST);

        return header;
    }

    private JPanel createTableCard() {
        JPanel card = UIHelper.createCardPanel();

        JLabel cardTitle = UIHelper.createSectionLabel("Reservation Records");
        cardTitle.setBorder(new EmptyBorder(4, 4, 12, 4));

        String[] columns = {"Reservation ID", "Vehicle Number", "Slot ID", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reservationTable = new JTable(tableModel);
        UIHelper.styleTable(reservationTable);
        reservationTable.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(tableModel);
        reservationTable.setRowSorter(sorter);
        reservationTable.setRowSelectionAllowed(true);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && reservationTable.getSelectedRow() != -1) {
                showReservationDetails();
            }
        });

        reservationTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        reservationTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        reservationTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        reservationTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        reservationTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = UIHelper.wrapScroll(reservationTable, UIHelper.PANEL_BG);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = UIHelper.createTransparentPanel();
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        totalReservationsLabel = new JLabel("Total Reservations: 0");
        totalReservationsLabel.setForeground(UIHelper.TEXT_MUTED);
        totalReservationsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        confirmedReservationsLabel = new JLabel("Confirmed: 0");
        confirmedReservationsLabel.setForeground(UIHelper.TEXT_MUTED);
        confirmedReservationsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        footer.add(totalReservationsLabel);
        footer.add(confirmedReservationsLabel);

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

        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (selectedStatus != null && !"All".equalsIgnoreCase(selectedStatus)) {
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(selectedStatus) + "$", 4));
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void createReservation() {
        String reservationId = JOptionPane.showInputDialog(this, "Enter Reservation ID:");
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return;
        }
        reservationId = reservationId.trim().toUpperCase();

        for (Reservation reservation : reservationService.getAllReservations()) {
            if (reservation.getReservationId().equalsIgnoreCase(reservationId)) {
                JOptionPane.showMessageDialog(this, "Reservation ID already exists.");
                return;
            }
        }

        String vehicleNumber = JOptionPane.showInputDialog(this, "Enter Vehicle Number:");
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            return;
        }

        String slotInput = JOptionPane.showInputDialog(this, "Enter Slot ID:");
        if (slotInput == null || slotInput.trim().isEmpty()) {
            return;
        }

        String time = JOptionPane.showInputDialog(this, "Enter Reservation Time:");
        if (time == null || time.trim().isEmpty()) {
            return;
        }

        String[] statusOptions = {"Confirmed", "Pending"};
        String status = (String) JOptionPane.showInputDialog(
                this,
                "Select Reservation Status:",
                "Reservation Status",
                JOptionPane.PLAIN_MESSAGE,
                null,
                statusOptions,
                statusOptions[0]
        );

        if (status == null || status.trim().isEmpty()) {
            return;
        }

        String slotId = slotInput.trim().toUpperCase();
        String vehicleNo = vehicleNumber.trim().toUpperCase();

        Reservation newReservation = new Reservation(
                reservationId,
                vehicleNo,
                slotId,
                time.trim(),
                status
        );
        newReservation.setNotes("Created from Reservation Panel");
        newReservation.addHistory(time.trim() + " - Reservation entered in system [" + status + "]");
        reservationService.addReservation(newReservation);

        refreshTable();
        if (onDataChanged != null) {
            onDataChanged.run();
        }

        JOptionPane.showMessageDialog(this, "Reservation created successfully.");
    }

    private void cancelReservation() {
        int selectedViewRow = reservationTable.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation row first.");
            return;
        }

        int selectedModelRow = reservationTable.convertRowIndexToModel(selectedViewRow);
        String reservationId = String.valueOf(tableModel.getValueAt(selectedModelRow, 0));
        Reservation selectedReservation = reservationService.getReservationById(reservationId);

        if (selectedReservation == null) {
            JOptionPane.showMessageDialog(this, "Reservation not found.");
            return;
        }

        if ("Cancelled".equalsIgnoreCase(selectedReservation.getStatus())) {
            JOptionPane.showMessageDialog(this, "This reservation is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel reservation " + selectedReservation.getReservationId() + "?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            reservationService.cancelReservation(selectedReservation.getReservationId());
            selectedReservation.addHistory("Status changed to Cancelled for reservation " + selectedReservation.getReservationId());
            selectedReservation.setNotes("Reservation cancelled by admin/user action");
            refreshTable();

            if (onDataChanged != null) {
                onDataChanged.run();
            }

            JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");
        }
    }

    private void showReservationDetails() {
        int selectedViewRow = reservationTable.getSelectedRow();
        if (selectedViewRow == -1) {
            return;
        }

        int selectedModelRow = reservationTable.convertRowIndexToModel(selectedViewRow);
        String reservationId = String.valueOf(tableModel.getValueAt(selectedModelRow, 0));
        Reservation selectedReservation = reservationService.getReservationById(reservationId);

        if (selectedReservation == null) {
            return;
        }

        Vehicle linkedVehicle = vehicleService.getVehicleByNumber(selectedReservation.getVehicleNumber());
        ParkingSlot linkedSlot = slotService.getSlotByDisplayId(selectedReservation.getSlotId());

        List<Reservation> vehicleReservations = reservationService.getReservationsByVehicle(selectedReservation.getVehicleNumber());
        List<Reservation> slotReservations = reservationService.getReservationsBySlot(selectedReservation.getSlotId());

        StringBuilder historyText = new StringBuilder();
        for (String event : selectedReservation.getHistory()) {
            historyText.append(event).append("\n");
        }

        StringBuilder vehicleReservationText = new StringBuilder();
        for (Reservation reservation : vehicleReservations) {
            vehicleReservationText.append("- ")
                    .append(reservation.getReservationId())
                    .append(" | ")
                    .append(reservation.getSlotId())
                    .append(" | ")
                    .append(reservation.getReservationTime())
                    .append(" | ")
                    .append(reservation.getStatus())
                    .append("\n");
        }

        StringBuilder slotReservationText = new StringBuilder();
        for (Reservation reservation : slotReservations) {
            slotReservationText.append("- ")
                    .append(reservation.getReservationId())
                    .append(" | ")
                    .append(reservation.getVehicleNumber())
                    .append(" | ")
                    .append(reservation.getReservationTime())
                    .append(" | ")
                    .append(reservation.getStatus())
                    .append("\n");
        }

        Object[] options = {"View 6-Month History", "Close"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Reservation ID: " + selectedReservation.getReservationId() +
                        "\nVehicle Number: " + selectedReservation.getVehicleNumber() +
                        "\nSlot ID: " + selectedReservation.getSlotId() +
                        "\nReservation Time: " + selectedReservation.getReservationTime() +
                        "\nStatus: " + selectedReservation.getStatus() +
                        "\nNotes: " + selectedReservation.getNotes() +
                        "\nLinked Vehicle Owner: " + (linkedVehicle != null ? linkedVehicle.getOwnerName() : "-") +
                        "\nLinked Vehicle Type: " + (linkedVehicle != null ? linkedVehicle.getVehicleType() : "-") +
                        "\nLinked Slot Block: " + (linkedSlot != null ? linkedSlot.getBlockName() : "-") +
                        "\nLinked Slot Group: " + (linkedSlot != null ? linkedSlot.getSlotGroup() : "-"),
                "Reservation Details",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (choice == 0) {
            JTextArea textArea = new JTextArea(
                    "Reservation ID: " + selectedReservation.getReservationId() +
                            "\nVehicle Number: " + selectedReservation.getVehicleNumber() +
                            "\nSlot ID: " + selectedReservation.getSlotId() +
                            "\nReservation Time: " + selectedReservation.getReservationTime() +
                            "\nStatus: " + selectedReservation.getStatus() +
                            "\n\nReservation Event History:\n" + historyText +
                            "\nVehicle Reservation History:\n" + vehicleReservationText +
                            "\nSlot Reservation History:\n" + slotReservationText
            );

            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textArea.setBackground(UIHelper.PANEL_BG);
            textArea.setForeground(UIHelper.TEXT_PRIMARY);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(620, 380));

            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "6-Month Reservation History",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        List<Reservation> reservations = reservationService.getAllReservations();
        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getVehicleNumber(),
                    r.getSlotId(),
                    r.getReservationTime(),
                    r.getStatus()
            });
        }

        totalReservationsLabel.setText("Total Reservations: " + reservations.size());
        confirmedReservationsLabel.setText("Confirmed: " + reservationService.getConfirmedReservationCount());
        applyFilters();
    }
}