package view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MultiLineHtmlRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			label.setText((String) value);
			label.setVerticalAlignment(JLabel.TOP);
			label.setHorizontalAlignment(JLabel.LEFT);
		}
		return c;
	}
}
