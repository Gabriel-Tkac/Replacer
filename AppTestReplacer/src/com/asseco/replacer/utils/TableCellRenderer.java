package com.asseco.replacer.utils;

import java.awt.Component;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 6194879501228687780L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		//override getTableCellRendererComponent(). This will always return a JLable instance.
		final JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		//A while ago we passed a File object to the object array. The value variable is the File object itself!
		if (value != null && value instanceof File) {
			File file = (File) value;
			String fileName = file.getName(); 
			String imgPath = "";
			if (fileName.contains(".xml"))
				imgPath = "/resources/XML.png";
			else if (fileName.contains(".txt"))
				imgPath = "/resources/TXT.png";
			else if (fileName.contains(".apptest"))
				imgPath = "/resources/apptest.png";
		    URL url = TableCellRenderer.class.getResource(imgPath);
		    ImageIcon icon = new ImageIcon(url);
		    label.setIcon(icon);
			//label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));//Use this method to set the proper icon for the file.
			label.setText(fileName);
		}

		return label;
	}

}