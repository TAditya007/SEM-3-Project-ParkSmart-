package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UIHelper {

    private UIHelper() {
    }

    public static final Color APP_BG = new Color(9, 14, 24);
    public static final Color CARD_BG = new Color(17, 24, 39);
    public static final Color PANEL_BG = new Color(21, 30, 48);
    public static final Color INPUT_BG = new Color(23, 33, 50);
    public static final Color BORDER_COLOR = new Color(42, 61, 89);
    public static final Color HEADER_BG = new Color(26, 40, 62);
    public static final Color BUTTON_BG = new Color(28, 44, 66);
    public static final Color ACCENT_BG = new Color(0, 184, 255);

    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(155, 205, 225);
    public static final Color TEXT_MUTED = new Color(180, 210, 230);
    public static final Color TEXT_SOFT = new Color(145, 175, 195);
    public static final Color TEXT_DARK = new Color(10, 18, 30);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font VALUE_FONT = new Font("Segoe UI", Font.BOLD, 28);

    public static JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        return panel;
    }

    public static JPanel createTransparentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(createCardBorder());
        return card;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        );
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(TITLE_FONT);
        return label;
    }

    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SECONDARY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return label;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(SECTION_FONT);
        return label;
    }

    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(LABEL_FONT);
        return label;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(BUTTON_BG);
        button.setForeground(TEXT_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        return button;
    }

    public static JButton createAccentButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(ACCENT_BG);
        button.setForeground(TEXT_DARK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        return button;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(BODY_FONT);
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 76, 104), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    public static JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(BODY_FONT);
        area.setBackground(CARD_BG);
        area.setForeground(new Color(220, 230, 240));
        area.setBorder(new EmptyBorder(8, 4, 4, 4));
        return area;
    }

    public static JScrollPane wrapScroll(Component component, Color viewportColor) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(viewportColor);
        return scrollPane;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(BODY_FONT);
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(45, 60, 82));
        table.setSelectionBackground(ACCENT_BG);
        table.setSelectionForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setReorderingAllowed(false);
    }

    public static JPanel createStatCard(String title, String value, String note) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(createCardBorder());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(170, 205, 225));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setFont(VALUE_FONT);

        JLabel noteLabel = new JLabel("<html><body style='width:200px'>" + note + "</body></html>");
        noteLabel.setForeground(TEXT_SOFT);
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(noteLabel, BorderLayout.SOUTH);

        return card;
    }
}
