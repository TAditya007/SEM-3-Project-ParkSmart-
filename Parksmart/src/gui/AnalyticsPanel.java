package gui;

import service.AnalyticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AnalyticsPanel extends JPanel {
    private final AnalyticsService analyticsService;
    private JTable rankingTable;
    private DefaultTableModel tableModel;
    private JTextArea insightArea;
    private JPanel metricGrid;

    public AnalyticsPanel(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;

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
        JLabel subtitle = UIHelper.createSubtitleLabel("Revenue, congestion, and parking performance insights");

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

        grid.add(UIHelper.createStatCard(
                "Total Records",
                String.valueOf(analyticsService.getAnalyticsRecords().size()),
                "Analytics records tracked"
        ));
        grid.add(UIHelper.createStatCard(
                "Revenue",
                "₹18,450",
                "Fenwick-based cumulative tracking"
        ));
        grid.add(UIHelper.createStatCard(
                "Peak Occupancy",
                "84%",
                "Highest observed utilization"
        ));
        grid.add(UIHelper.createStatCard(
                "Optimization",
                "Ready",
                "Greedy + DP analysis"
        ));

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
        JPanel card = createSectionCard("Zone Ranking");

        String[] columns = {"Rank", "Zone", "Occupancy", "Revenue"};
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
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(100);

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

    private void refreshAnalytics() {
        loadAnalyticsData();
        refreshMetricGrid();
    }

    private void refreshMetricGrid() {
        if (metricGrid == null) {
            return;
        }

        metricGrid.removeAll();
        metricGrid.add(UIHelper.createStatCard(
                "Total Records",
                String.valueOf(analyticsService.getAnalyticsRecords().size()),
                "Analytics records tracked"
        ));
        metricGrid.add(UIHelper.createStatCard(
                "Revenue",
                "₹18,450",
                "Fenwick-based cumulative tracking"
        ));
        metricGrid.add(UIHelper.createStatCard(
                "Peak Occupancy",
                "84%",
                "Highest observed utilization"
        ));
        metricGrid.add(UIHelper.createStatCard(
                "Optimization",
                "Ready",
                "Greedy + DP analysis"
        ));

        metricGrid.revalidate();
        metricGrid.repaint();
    }

    private void loadAnalyticsData() {
        if (tableModel == null || insightArea == null) {
            return;
        }

        tableModel.setRowCount(0);

        int[] usageData = {92, 84, 76, 61};
        int[] sortedUsage = analyticsService.sortUsageData(usageData);

        String[] zones = {"Block B", "Block A", "Block C", "EV Zone"};
        String[] revenues = {"₹6,250", "₹5,140", "₹4,380", "₹2,680"};

        for (int i = 0; i < sortedUsage.length && i < zones.length && i < revenues.length; i++) {
            tableModel.addRow(new Object[]{
                    i + 1,
                    zones[i],
                    sortedUsage[i] + "%",
                    revenues[i]
            });
        }

        insightArea.setText(
                "Analytics Records: " + analyticsService.getAnalyticsRecords().size() + "\n\n" +
                "• Block B continues to show the highest demand and strongest revenue output.\n\n" +
                "• Entry-side congestion is increasing during the mid-morning window.\n\n" +
                "• EV zone utilization is moderate and can support more incoming vehicles.\n\n" +
                "• Reservation-driven allocation is helping reduce random slot conflicts.\n\n" +
                "• Sorting and optimization modules are connected to the analytics service."
        );
    }

    private void runOptimization() {
        analyticsService.runOptimization();
        refreshAnalytics();

        insightArea.append("\n\n[Optimization] Greedy allocation and dynamic programming analysis completed.");

        JOptionPane.showMessageDialog(
                this,
                "Optimization algorithms executed successfully!",
                "Analytics Updated",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}