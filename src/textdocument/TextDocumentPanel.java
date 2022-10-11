package textdocument;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import spellcheck.SpellCheckManager;
import spellcheck.SpellCheckPanel;

/* Responsible for displaying the TextComponent
 * Contains a menu that aids in editing the TextComponent
 * Also has a SpellCheckManager that checks the text's spelling */
public class TextDocumentPanel extends JPanel {
	private static final long serialVersionUID = 3674629504976423414L;
	
	private final JScrollPane scrollPane;
	private final JTextPane textPane;
	private File file;
	
	private final TextDocumentHistoryManager textDocHistoryManager;
	private final JMenu editMenu, spCheckMenu;
	private final JMenuItem undoItem, redoItem, cutItem, copyItem, pasteItem, allItem, runItem, configItem;
	private final SpellCheckManager spCheckManager;
	
	{
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textPane = new JTextPane();
		scrollPane.getViewport().add(textPane);
		add(scrollPane, "Center");
		
		textDocHistoryManager = new TextDocumentHistoryManager(textPane.getDocument());
		
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		
		undoItem = textDocHistoryManager.getUndoMenuItem();
		redoItem = textDocHistoryManager.getRedoMenuItem();
		
		cutItem = new JMenuItem("Cut");
		cutItem.setMnemonic('C');
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textPane.cut();
			}
		});
		
		copyItem = new JMenuItem("Copy");
		copyItem.setMnemonic('O');
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textPane.copy();
			}
		});
		
		pasteItem = new JMenuItem("Paste");
		pasteItem.setMnemonic('P');
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textPane.paste();
			}
		});
		
		allItem = new JMenuItem("Select All");
		allItem.setMnemonic('A');
		allItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		allItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textPane.selectAll();
			}
		});
		
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.add(new JSeparator());
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(new JSeparator());
		editMenu.add(allItem);
		
		spCheckManager = new SpellCheckManager();
		spCheckMenu = new JMenu("SpellChecker");
		spCheckMenu.setMnemonic('K');
		
		runItem = new JMenuItem("Run");
		runItem.setMnemonic('R');
		runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		runItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel spCheckPanel = spCheckManager.getSpellCheckPanel();
				
				if (TextDocumentPanel.this.getComponentCount() == 1) {  //'this' points to ActionListener, so need TextDocumentPanel.this
					TextDocumentPanel.this.add(spCheckPanel, "East");
				}
				
				if (spCheckPanel instanceof SpellCheckPanel) {
					((SpellCheckPanel)spCheckPanel).runSpellCheck(textPane);
				}
				
				revalidate();
				repaint();
			}
		});
		
		configItem = new JMenuItem("Configure");
		configItem.setMnemonic('C');
		configItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (TextDocumentPanel.this.getComponentCount() == 1) {  //'this' points to ActionListener, so need TextDocumentPanel.this
					TextDocumentPanel.this.add(spCheckManager.getConfigPanel(), "East");
				}
				
				revalidate();
				repaint();
			}
		});
		
		spCheckMenu.add(runItem);
		spCheckMenu.add(configItem);
	}
	
	//Reads in a text file
	public TextDocumentPanel() {}  //simple constructor; not used
	public TextDocumentPanel(File inFile) throws IOException {
		file = inFile;
		FileReader fr = new FileReader(inFile);
		textPane.read(fr, "");
		fr.close();
	}
	
	public JMenu getEditMenu() {
		return editMenu;
	}
	
	public JMenu getSpellCheckMenu() {
		return spCheckMenu;
	}
	
	public File getFile() {
		return file;
	}
	
	public void save(File inFile) throws IOException {
		file = inFile;
		FileWriter fw = new FileWriter(file);
		fw.write(textPane.getText());
		fw.close();
	}
}