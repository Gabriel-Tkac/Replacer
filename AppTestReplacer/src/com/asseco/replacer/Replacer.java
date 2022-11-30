package com.asseco.replacer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import com.asseco.replacer.model.FileOccurence;
import com.asseco.replacer.model.Replace;
import com.asseco.replacer.utils.Logger;
import com.asseco.replacer.utils.TableCellRenderer;


public class Replacer extends JFrame {
	
	private static final long serialVersionUID = 734680246451578545L;
	
	private static JMenuBar menuBar;
	private JTextField original1, original2, original3, replace1, replace2, replace3;
	// Tabulka poloziek
	public static JTable table;
	protected static TableModel dataModel;
	private JButton search, replace;
	private static Replacer replacer;
	
	Toolkit t;
	int xSize = 950;
	int ySize = 600;
	
	private List<File> files;
	private List<FileOccurence> occurences;
	
	private static Logger log;
	
	public Replacer() {
		super.setTitle("Replacer");
		
		log = new Logger();
		log.write("Aplikacia spustena " + log.time());
		
		replacer = this;
		occurences = new ArrayList<FileOccurence>();
		
		t = Toolkit.getDefaultToolkit();
		URL url = getClass().getResource("/resources/apptest.png"); // pevne urceny zdroj obrazka
		Image img = t.getImage(url);
		this.setIconImage(img);
		Dimension d = t.getScreenSize();
		this.setBackground(Color.LIGHT_GRAY);
		this.setLocation(d.width/2-xSize/2, d.height/2-ySize/2);
		
		this.setLayout(new BorderLayout(10, 10));
		
		// MENU
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("S˙bory");
		menuBar.add(fileMenu);
			
		JMenuItem open = new JMenuItem("Otvoriù adres·r");
		open.addActionListener(new OpenAL());
		
		JMenuItem end = new JMenuItem("Koniec");
		end.addActionListener(new EndAL());
		
		fileMenu.add(open);
		fileMenu.add(end);
		
		// TEXTOVE POLIA
		JPanel fieldPanel = new JPanel(new GridLayout(6, 1, 5, 5));
		
		// Prve nahradenie
		JPanel firstReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel firstReplaceLabel = new JLabel("1. text na nahradenie");
		original1 = new JTextField(70);
		original1.setToolTipText("Text, ktor˝ sa m· nahradiù");
		firstReplacePanel.add(firstReplaceLabel);
		firstReplacePanel.add(original1);
		
		JPanel secondReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel firstReplaceWithLabel = new JLabel("1. nov˝ text");
		replace1 = new JTextField(70);
		replace1.setToolTipText("Nov˝ text");
		secondReplacePanel.add(firstReplaceWithLabel);
		secondReplacePanel.add(replace1);
		
		// Druhe nahradenie
		JPanel thirdReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel secondReplaceLabel = new JLabel("2. text na nahradenie");
		original2 = new JTextField(70);
		original2.setToolTipText("Text, ktor˝ sa m· nahradiù");
		thirdReplacePanel.add(secondReplaceLabel);
		thirdReplacePanel.add(original2);
		
		JPanel fourthReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel secondReplaceWithLabel = new JLabel("2. nov˝ text");
		replace2 = new JTextField(70);
		replace2.setToolTipText("Nov˝ text");
		fourthReplacePanel.add(secondReplaceWithLabel);
		fourthReplacePanel.add(replace2);
		
		// Tretie nahradenie
		JPanel fifthReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel thirdReplaceLabel = new JLabel("3. text na nahradenie");
		original3 = new JTextField(70);
		original3.setToolTipText("Text, ktor˝ sa m· nahradiù");
		fifthReplacePanel.add(thirdReplaceLabel);
		fifthReplacePanel.add(original3);
		
		JPanel sixthReplacePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel thirdReplaceWithLabel = new JLabel("3. nov˝ text");
		replace3 = new JTextField(70);
		replace3.setToolTipText("Nov˝ text");
		sixthReplacePanel.add(thirdReplaceWithLabel);
		sixthReplacePanel.add(replace3);
		
		fieldPanel.add(firstReplacePanel);
		fieldPanel.add(secondReplacePanel);
		fieldPanel.add(thirdReplacePanel);
		fieldPanel.add(fourthReplacePanel);
		fieldPanel.add(fifthReplacePanel);
		fieldPanel.add(sixthReplacePanel);
		
		this.add(fieldPanel, BorderLayout.NORTH);
		
		// TABULKA
		String[] hlavicka = {"S˙bor", "V˝skyt hæadanÈho textu"};
		String[][] data = new String[10][2];
		for (int i = 0; i< 10;i++) {
			data[i][0] = "";
			data[i][1] = "";
		}
		dataModel = new DefaultTableModel(data, hlavicka) {
			private static final long serialVersionUID = 2810882655549898909L;
			public boolean isCellEditable(int row, int column){  
		        return false;  
		    }
		};
		
		table = new JTable(dataModel);
		table.addMouseListener(new OpenDocumentML());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//dataModel.addTableModelListener(new TableML());
		
		JScrollPane scrollPaneTable = new JScrollPane(table);
		scrollPaneTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneTable.setBackground(Color.white);
	    
	    TableColumn column =  table.getColumnModel().getColumn(0);
	    column.setPreferredWidth(500);
	    column.setCellRenderer(new TableCellRenderer());
	    column =  table.getColumnModel().getColumn(1);
	    column.setPreferredWidth(300);
	    
	    this.add(scrollPaneTable, BorderLayout.CENTER);
		
		
		// TLACIDLA 
		search = new JButton("Hæadaù");
		search.addActionListener(new SearchAL());
		
		replace = new JButton("Nahradiù");
		replace.addActionListener(new ReplaceAL());
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
		buttonPanel.add(new JLabel("          "));
		buttonPanel.add(new JLabel("          "));
		buttonPanel.add(new JLabel("          "));
		buttonPanel.add(search);
		buttonPanel.add(replace);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		this.setSize(xSize, ySize);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//replacer.setVisible(false);
				replacer = null;
			}
		});
		
	}
	
	class OpenAL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			files = new ArrayList<File>();
			
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			
			int returnVal = fc.showOpenDialog(fc);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File dir = fc.getSelectedFile();
				log.write("Nacitany adresar: " + dir.getAbsolutePath());
				File[] filesArr = dir.listFiles();
				for (File file: filesArr) {
					if (!file.isDirectory())
						files.add(file);
				}
				
				log.write("Pocet suborov vo zvolenom adresari: " + String.valueOf(files.size()));
	        }
	        
	        if (files.size() > 0 ) {
	        	DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.setRowCount(0);
				
				for (File file: files) {
					Object[] dataRiadku = {file, ""};
					model.addRow(dataRiadku);
				}
	        }
	        
		}
	}
	
	class EndAL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			replacer.setVisible(false);
			replacer = null;
		}
	}
	
	class OpenDocumentML extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			boolean isSelected = table.getSelectionModel().isSelectionEmpty();
			if (!isSelected) {
				ListSelectionModel selection = table.getSelectionModel();
				int index = selection.getLeadSelectionIndex();
				Object object = table.getModel().getValueAt(index, 0);
				if (object instanceof File && e.getClickCount() == 2) {
					File file = (File) object;
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException e1) {
						log.writeException("Chyba pri otvarani suboru " + file.getName(), e1, true);
					}
				}
			}
		}
	}
	
	class SearchAL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Ak uz bolo tlacidlo pouzite, vycistime vyskyty
			clearOccurencies();
			
			searchAndReplace();
			
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
			
			if (occurences.size() > 0) {
				for (FileOccurence occurence: occurences) {
					Object[] dataRiadku = {occurence.getFile(), occurence.toString()};
					model.addRow(dataRiadku);
				}
			}
			else {
				JOptionPane.showMessageDialog(replacer, "Neboli n·jdenÈ ûiadne v˝skyty hæadanÈho textu", "éiadne v˝skyty", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class ReplaceAL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearOccurencies();
			searchAndReplace();
			if (!occurences.isEmpty()) {
				int i = 0;
				for (FileOccurence occurence: occurences) {
					File file = occurence.getFile();
					List<String> lines = processFile(file);
					List<Replace> replacements = occurence.getReplaces();
					
					for (Replace replace: replacements) {
						log.write(" ============== Vykonavame nahradzanie v subore " + file.getName() + " ========================");
						log.write("Riadok " + replace.getLine() + ", text " + replace.getReplaceWhat() + ", novy text " + replace.getReplaceWith());
						if (!replace.getReplaceWith().isEmpty()) {
							String changed = lines.get(replace.getLine()).replace(replace.getReplaceWhat(), replace.getReplaceWith());
							lines.set(replace.getLine(), changed);
							i++;
						}
					}
					
					try {
						FileOutputStream fos = new FileOutputStream(file);
						byte[] newLine = {'\n'};
						for (String line: lines) {
							fos.write(line.getBytes(StandardCharsets.UTF_8));
							fos.write(newLine);
						}
						
						fos.close();
					}
					catch (IOException e2) {
						log.writeException("Chyba pri zapisovani zmeneneho suboru: " + occurence.getFileName(), e2, true);
					}
				}
				JOptionPane.showMessageDialog(replacer, "Bolo vykonan˝ch " + String.valueOf(i) + " nahradenÌ textu");
			}
			else {
				JOptionPane.showMessageDialog(replacer, "Neboli n·jdenÈ ûiadne v˝skyty hæadanÈho textu", "éiadne v˝skyty", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Prevedie textovy subor na oznam riadkov
	 * @param file - vstupny textovy subo
	 * @return zoznam riadkov suboru
	 */
	List<String> processFile(File file) {
		List<String> lines = new ArrayList<String>();
		try {
			byte[] bytes = Files.readAllBytes(file.toPath());
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			
			br.close();
		}
		catch (IOException e) {
			log.writeException("Chyba pri citani suboru " + file.getName(), e, true);
		}
		return lines;
	}
	
	/**
	 * Nacita hodnoty textovych poli, vyhlada zadane vyskyty a vytvori objekty FileOccurance
	 */
	public void searchAndReplace() {
		String strToSearch1 = original1.getText() != null ? original1.getText() : "";
		String strToSearch2 = original2.getText() != null ? original2.getText() : "";
		String strToSearch3 = original3.getText() != null ? original3.getText() : "";
		
		List<String> strToSearch = new ArrayList<String>();
		strToSearch.add(strToSearch1);
		strToSearch.add(strToSearch2);
		strToSearch.add(strToSearch3);
		
		String strToReplace1 = replace1.getText() != null ? replace1.getText() : "";
		String strToReplace2 = replace2.getText() != null ? replace2.getText() : "";
		String strToReplace3 = replace3.getText() != null ? replace3.getText() : "";
		
		List<String> strToReplace = new ArrayList<String>();
		strToReplace.add(strToReplace1);
		strToReplace.add(strToReplace2);
		strToReplace.add(strToReplace3);
		
		for (File file: files) {
			String fileName = file.getName();
			FileOccurence occurence = new FileOccurence(file);
			log.write(" =============== Search - spracuvame subor " + fileName + " ===========================");
			if (fileName.endsWith(".txt") || fileName.endsWith(".xml") ||
				fileName.endsWith(".apptest") || fileName.endsWith("apptest-config")) {
				
				List<String> lines = processFile(file);
				for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
					for (int replaceIndex = 0; replaceIndex < 3; replaceIndex++) {
							if (!strToSearch.get(replaceIndex).isEmpty()) {
								if (lines.get(lineIndex).contains(strToSearch.get(replaceIndex))) {
								log.write("Je pritomny text: " + strToSearch.get(replaceIndex) + " na riadku " + lineIndex);
								occurence.addReplace(new Replace(strToSearch.get(replaceIndex), strToReplace.get(replaceIndex), lineIndex));
							}
						}
					}
				}
			}
			else {
				log.write("Subor " + fileName + " nie je podporovany. Mozne typy suborov: txt, xml, apptest, apptest-config");
			}
			
			if (occurence.getReplaces().size() > 0) {
				log.write("Subor ma vyskyt hladaneho textu");
				occurences.add(occurence);
			}
		}
	}
	
	/**
	 * Vymaze existujuce objekty FileOccurance
	 */
	public void clearOccurencies() {
		if (!occurences.isEmpty())
			for (@SuppressWarnings("unused") FileOccurence occ: occurences)
				occ = null;
		occurences.clear();
		System.gc();
	}

	public static void main(String[] args) {
		replacer = new Replacer();
		replacer.setVisible(true);
		
		while (replacer != null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.writeException("Signer - signWithGUI: ", e, true);
			}
		}
		
		log.close();
		System.exit(0);

	}

}
