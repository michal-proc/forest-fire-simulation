package simulation.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TextAreaRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JTextArea textArea = new JTextArea(value.toString());
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textArea.setOpaque(true);
        textArea.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return textArea;
    }
}