package gui;

import service.RouteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoutePanel extends JPanel {
    private final RouteService routeService;

    private JComboBox<String> sourceBox;
    private JComboBox<String> destinationBox;
    private JTextArea routeInfoArea;
    private MapPanel mapPanel;
    

    private boolean previewReady = false;

    public RoutePanel(RouteService routeService) {
        this.routeService = routeService;
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

        JLabel title = UIHelper.createTitleLabel("Shortest Path Finder");
        JLabel subtitle = UIHelper.createSubtitleLabel(
                "Select entrance and destination to view the best parking route"
        );

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JButton findBtn = UIHelper.createButton("Find Route");
        findBtn.addActionListener(e -> findRoute());

        top.add(titleBox, BorderLayout.WEST);
        top.add(findBtn, BorderLayout.EAST);

        return top;
    }

    private JPanel createMainSection() {
        JPanel main = UIHelper.createTransparentPanel();
        main.setLayout(new GridLayout(1, 2, 18, 18));

        JPanel controlCard = createControlCard();
        JPanel mapCard = createMapCard();

        main.add(controlCard);
        main.add(mapCard);

        previewReady = true;
        updatePreview();

        return main;
    }

    private JPanel createControlCard() {
        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout(12, 12));

        JPanel form = UIHelper.createTransparentPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel sourceLabel = UIHelper.createFieldLabel("Source");

        sourceBox = new JComboBox<>();
        sourceBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel destLabel = UIHelper.createFieldLabel("Destination");

        destinationBox = new JComboBox<>();
        destinationBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        sourceBox.addActionListener(e -> {
            if (previewReady) {
                updatePreview();
            }
        });

        destinationBox.addActionListener(e -> {
            if (previewReady) {
                updatePreview();
            }
        });

        

        form.add(sourceLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(sourceBox);
        form.add(Box.createVerticalStrut(14));
        form.add(destLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(destinationBox);
        form.add(Box.createVerticalStrut(18));
        
        routeInfoArea = UIHelper.createTextArea();
        routeInfoArea.setEditable(false);
        routeInfoArea.setText("Route details will appear here.");

        card.add(form, BorderLayout.NORTH);
        card.add(new JScrollPane(routeInfoArea), BorderLayout.CENTER);

        loadNodesIntoBoxes();

        return card;
    }
    private JPanel createMapCard() {
        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());

        JPanel topBar = UIHelper.createTransparentPanel();
        topBar.setLayout(new BorderLayout());

        JLabel title = UIHelper.createSectionLabel("Map View");
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel zoomPanel = UIHelper.createTransparentPanel();
        JButton zoomInBtn = UIHelper.createButton("+");
        JButton zoomOutBtn = UIHelper.createButton("-");

        mapPanel = new MapPanel();
        mapPanel.setPreferredSize(new Dimension(600, 420));

        zoomInBtn.addActionListener(e -> mapPanel.adjustZoom(0.1));
        zoomOutBtn.addActionListener(e -> mapPanel.adjustZoom(-0.1));

        zoomPanel.add(zoomOutBtn);
        zoomPanel.add(Box.createHorizontalStrut(8));
        zoomPanel.add(zoomInBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(zoomPanel, BorderLayout.EAST);

        card.add(topBar, BorderLayout.NORTH);
        card.add(mapPanel, BorderLayout.CENTER);

        return card;
    }

    private void findRoute() {
        String source = (String) sourceBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();
        if (source == null || destination == null) {
            JOptionPane.showMessageDialog(this, "Please select source and destination.");
            return;
        }
        if (source.equals(destination)) {
            JOptionPane.showMessageDialog(this, "Source and destination cannot be same.");
            return;
        }
        List<String> path = routeService.findShortestPath(source, destination);
        if (path == null || path.isEmpty()) {
            routeInfoArea.setText("No route found.");
            mapPanel.setRoute(Collections.emptyList());
            return;
        }
        routeInfoArea.setText(
                "Source: " + source + "\n" +
                "Destination: " + destination + "\n" +
                "Path: " + String.join(" -> ", path) + "\n" +
                "Stops: " + (path.size() - 1)
        );
        mapPanel.setRoute(path);
        updatePreview();
    }

    private class MapPanel extends JPanel {
        private final Map<String, Point> points = new LinkedHashMap<>();
        private final List<String> route = new ArrayList<>();
        
        
        private float dashOffset = 0f;
        private final javax.swing.Timer animationTimer;

        private final int baseW = 780;
        private final int baseH = 460;

        private double zoom = 1.0;
        private final int centerX = 390;
        private final int centerY = 235;
        private final int pentagonRadius = 145;

        MapPanel() {
            setBackground(new Color(13, 20, 34));
            buildLayout();
            animationTimer = new javax.swing.Timer(60, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dashOffset += 2.5f;
                    repaint();
                }
            });
            animationTimer.start();
        }

        private void buildLayout() {
            points.clear();

            points.put("ALPHA", pointOnCircle(centerX, centerY, pentagonRadius, -90));
            points.put("BETA", pointOnCircle(centerX, centerY, pentagonRadius, -18));
            points.put("GAMMA", pointOnCircle(centerX, centerY, pentagonRadius, 54));
            points.put("DELTA", pointOnCircle(centerX, centerY, pentagonRadius, 126));
            points.put("EPSILON", pointOnCircle(centerX, centerY, pentagonRadius, 198));
        
            points.put("MAIN_ENTRANCE", new Point(95, 85));
            points.put("MAIN_EXIT", new Point(95, 385));
        
           
        }

        private Point pointOnCircle(int cx, int cy, int r, double angleDeg) {
            double rad = Math.toRadians(angleDeg);
            int x = (int) Math.round(cx + r * Math.cos(rad));
            int y = (int) Math.round(cy + r * Math.sin(rad));
            return new Point(x, y);
        }
       
        void setRoute(List<String> newRoute) {
            route.clear();
            if (newRoute != null) route.addAll(newRoute);
            repaint();
        }

        void adjustZoom(double delta) {
            zoom += delta;
            if (zoom < 0.75) zoom = 0.75;
            if (zoom > 1.35) zoom = 1.35;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform old = g2.getTransform();

            double sx = getWidth() / (double) baseW;
            double sy = getHeight() / (double) baseH;
            double scale = Math.min(sx, sy);

            g2.translate(getWidth() / 2.0, getHeight() / 2.0);
            g2.scale(scale * zoom, scale * zoom);
            g2.translate(-baseW / 2.0, -baseH / 2.0);

            drawBackground(g2);
            drawRoads(g2);
            drawBlockZones(g2);
            drawRoute(g2);
            drawNodes(g2);

            g2.setTransform(old);
            g2.dispose();
        }

        private void drawBackground(Graphics2D g2) {
            g2.setPaint(new GradientPaint(0, 0, new Color(10, 16, 26),
                    0, baseH, new Color(16, 24, 38)));
            g2.fillRect(0, 0, baseW, baseH);
            g2.setColor(new Color(24, 35, 54));
            for (int x = 0; x < baseW; x += 36) g2.drawLine(x, 0, x, baseH);
            for (int y = 0; y < baseH; y += 36) g2.drawLine(0, y, baseW, y);
        }

        private void drawBlockZones(Graphics2D g2) {
            drawZoneNear(g2, "Alpha", points.get("ALPHA"), -55, 35);
            drawZoneNear(g2, "Beta", points.get("BETA"), -35, 30);
            drawZoneNear(g2, "Gamma", points.get("GAMMA"), -30, 25);
            drawZoneNear(g2, "Delta", points.get("DELTA"), -25, 30);
            drawZoneNear(g2, "Epsilon", points.get("EPSILON"), -55, 30);
        }

        private void drawZoneNear(Graphics2D g2, String name, Point p, int dx, int dy) {
            if (p == null) return;
            drawZone(g2, name, p.x + dx, p.y + dy);
        }

        private void drawZone(Graphics2D g2, String name, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

            g2.setColor(new Color(20, 30, 48));
            g2.fillRoundRect(x, y, 120, 82, 16, 16);
            g2.setColor(new Color(85, 110, 145));
            g2.drawRoundRect(x, y, 120, 82, 16, 16);

            g2.setColor(Color.WHITE);
            g2.drawString(name, x + 10, y + 18);

            g2.setColor(new Color(0, 180, 255));
            g2.drawString("A(50)", x + 10, y + 38);

            g2.setColor(new Color(255, 200, 0));
            g2.drawString("B(100)", x + 10, y + 56);

            g2.setColor(new Color(0, 230, 130));
            g2.drawString("EV(30)", x + 10, y + 74);
        }

        private void drawRoads(Graphics2D g2) {
            g2.setColor(new Color(78, 95, 120));
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            connect(g2, "MAIN_ENTRANCE", "ALPHA");
            connect(g2, "ALPHA", "BETA");
            connect(g2, "BETA", "GAMMA");
            connect(g2, "GAMMA", "DELTA");
            connect(g2, "DELTA", "EPSILON");
            connect(g2, "EPSILON", "ALPHA");
            connect(g2, "DELTA", "MAIN_EXIT");
        }

        private void drawRoute(Graphics2D g2) {
            if (route.size() < 2) return;
            Stroke old = g2.getStroke();
            g2.setColor(new Color(0, 210, 255));
            g2.setStroke(new BasicStroke(
                    7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    10f, new float[]{18f, 14f}, dashOffset
            ));
            for (int i = 0; i < route.size() - 1; i++) {
                Point p1 = points.get(route.get(i));
                Point p2 = points.get(route.get(i + 1));
                if (p1 != null && p2 != null) g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            g2.setStroke(old);
        }

        private void drawNodes(Graphics2D g2) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));

            for (Map.Entry<String, Point> entry : points.entrySet()) {
                String name = entry.getKey();
                Point p = entry.getValue();
                boolean onRoute = route.contains(name);

                Color fill;
                if ("MAIN_ENTRANCE".equals(name)) fill = new Color(0, 180, 90);
                else if ("MAIN_EXIT".equals(name)) fill = new Color(220, 70, 70);
                else fill = new Color(190, 145, 45);

                if (onRoute) fill = new Color(0, 210, 255);

                g2.setColor(fill);
                g2.fillOval(p.x - 22, p.y - 22, 44, 44);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(p.x - 22, p.y - 22, 44, 44);

                g2.drawString(name, p.x - 32, p.y - 30);
            }
        }
        
        private void connect(Graphics2D g2, String a, String b) {
            Point p1 = points.get(a);
            Point p2 = points.get(b);
            if (p1 != null && p2 != null) g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void loadNodesIntoBoxes() {
        previewReady = false;

        List<String> nodes = routeService.getNodeNames();

        if (nodes == null || nodes.isEmpty()) {
            nodes = new ArrayList<>();
            Collections.addAll(
                    nodes,
                    "MAIN_ENTRANCE", "ALPHA", "BETA", "GAMMA", "DELTA", "EPSILON", "MAIN_EXIT"
            );
        }

        DefaultComboBoxModel<String> sourceModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> destinationModel = new DefaultComboBoxModel<>();

        for (String node : nodes) {
            sourceModel.addElement(node);
            destinationModel.addElement(node);
        }

        sourceBox.setModel(sourceModel);
        destinationBox.setModel(destinationModel);

        if (sourceModel.getSize() > 0) {
            sourceBox.setSelectedIndex(0);
        }

        if (destinationModel.getSize() > 1) {
            destinationBox.setSelectedIndex(1);
        } else if (destinationModel.getSize() > 0) {
            destinationBox.setSelectedIndex(0);
        }

        previewReady = true;
        updatePreview();
    }

    private void updatePreview() {
        if (!previewReady || sourceBox == null || destinationBox == null
                || routeInfoArea == null || mapPanel == null) {
            return;
        }

        String source = (String) sourceBox.getSelectedItem();
        String destination = (String) destinationBox.getSelectedItem();

        if (source == null || destination == null) {
            routeInfoArea.setText("Please select source and destination.");
            mapPanel.setRoute(Collections.emptyList());
            return;
        }

        if (source.equals(destination)) {
            routeInfoArea.setText(
                    "Source: " + source + "\n" +
                    "Destination: " + destination + "\n" +
                    "Select two different nodes."
            );
            mapPanel.setRoute(Collections.emptyList());
            return;
        }

        List<String> path = routeService.findShortestPath(source, destination);

        if (path == null || path.isEmpty()) {
            routeInfoArea.setText(
                    "Source: " + source + "\n" +
                    "Destination: " + destination + "\n" +
                    "No route found."
            );
            mapPanel.setRoute(Collections.emptyList());
            return;
        }

        routeInfoArea.setText(
                "Source: " + source + "\n" +
                "Destination: " + destination + "\n" +
                "Path: " + String.join(" -> ", path) + "\n" +
                "Stops: " + (path.size() - 1)
        );

        mapPanel.setRoute(path);
        mapPanel.revalidate();
        mapPanel.repaint();
    }
}   