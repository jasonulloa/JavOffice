package textdocument;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import filechooser.TypeFileChooser;

public class TextDocumentManager extends JTabbedPane {
	private static final long serialVersionUID = 4649936834531540925L;
	
	//menu for the document manager
	private final JMenuBar menuBar;
	private final JMenu fileMenu;
	private final JMenuItem newMenuItem, openMenuItem, saveMenuItem, closeMenuItem;
	
	{
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		newMenuItem = new JMenuItem("New");
		newMenuItem.setMnemonic('N');
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel newPanel = new TextDocumentPanel();
				TextDocumentManager.this.addTab("New", newPanel);  //'this' points to ActionListener, so need TextDocumentManager.this
				TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount() - 1);
			}
		});
		
		openMenuItem = new JMenuItem("Open");
		openMenuItem.setMnemonic('O');
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		openMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TypeFileChooser textChooser = new TypeFileChooser("Text files (*.txt)", ".txt");
				textChooser.setDialogTitle("Open File...");
				int returnValue = textChooser.showOpenDialog(null);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = textChooser.getSelectedFile();
					
					if (!isFileOpen(file)) {
						try {
							TextDocumentPanel openPanel = new TextDocumentPanel(file);
							TextDocumentManager.this.addTab(file.getName(), openPanel);  //'this' points to ActionListener, so need TextDocumentManager.this
							TextDocumentManager.this.setSelectedIndex(TextDocumentManager.this.getTabCount() - 1);
						}
						catch (IOException ioe) {
							JOptionPane.showMessageDialog(null, file.getName() + " could not be read.", "File Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						selectTabByFile(file);
						JOptionPane.showMessageDialog(null, file.getName() + " is already open.");
					}
				}
			}
		});
		
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setMnemonic('S');
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		saveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TextDocumentPanel savePanel = getActiveDocumentPanel();
				
				if (savePanel == null) {
					return;
				}
				
				TypeFileChooser textChooser = new TypeFileChooser("Text files (*.txt)", ".txt");
				textChooser.setDialogTitle("Save File...");
				int returnValue = textChooser.showSaveDialog(null);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = textChooser.getSelectedFile();
					boolean willSave = true;
					
					if (file.exists()) {  //'this' points to ActionListener, so need TextDocumentManager.this
						int confirm = JOptionPane.showConfirmDialog(TextDocumentManager.this, file.getName() + " already exists.\n"
							+ "Do you want to overwrite it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						
						if (confirm != 0) {
							willSave = false;
						}
					}
					
					if (willSave) {
						if (!file.getName().endsWith(".txt")) {
							JOptionPane.showMessageDialog(null, "This is not a text file (*.txt).", "Save Error", JOptionPane.ERROR_MESSAGE);
						}
						else {
							try {
								getActiveDocumentPanel().save(file);
								setActiveTabText(file.getName());
							}
							catch (IOException ioe) {
								JOptionPane.showMessageDialog(null, file.getName() + " could not be saved.", "Save Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});
		
		closeMenuItem = new JMenuItem("Close");
		closeMenuItem.setMnemonic('C');
		closeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selected = TextDocumentManager.this.getSelectedIndex();  //'this' points to ActionListener, so need TextDocumentManager.this
				
				if (selected == -1) {
					return;
				}
				
				TextDocumentManager.this.remove(selected);
			}
		});
		
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				refreshMenuBar();
			}
		});
		
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(closeMenuItem);
	}
	
	//Takes a JMenubar from the parent frame so the user can interact with it
	public TextDocumentManager(JMenuBar inMenuBar) {
		menuBar = inMenuBar;
		refreshMenuBar();
	}
	
	//Returns the DocumentPanel that is currently being worked on
	public TextDocumentPanel getActiveDocumentPanel() {
		int selected = getSelectedIndex();
		
		if (selected == -1) {
			return null;
		}
		
		Component toReturn = getComponentAt(selected);
		
		if (toReturn instanceof TextDocumentPanel) {
			return (TextDocumentPanel)toReturn;
		}
		else {
			return null;
		}
	}
	
	//Focuses on tab that is dealing with the file
	private void selectTabByFile(File file) {
		int index = -1;
		
		for (int i = 0; i < getComponentCount(); ++i) {
			Component atIndex = getComponentAt(i);
			
			if (atIndex instanceof TextDocumentPanel) {
				if (file.equals(((TextDocumentPanel)atIndex).getFile())) {
					index = i;
					break;
				}
			}
		}
		
		if (index != -1) {
			setSelectedIndex(index);
		}
	}
	
	//Changes name of tab
	private void setActiveTabText(String title) {
		int selected = getSelectedIndex();
		
		if (selected == -1) {
			return;
		}
		
		this.setTitleAt(selected, title);
	}
	
	//Checks if document is already open
	public boolean isFileOpen(File file) {
		for (Component comp : TextDocumentManager.this.getComponents()) {  //making sure 'this' points to TextDocumentManager
			if (comp instanceof TextDocumentPanel) {  //check only TextDocumentPanels
				if (file.equals(((TextDocumentPanel)comp).getFile())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	//Called when documents are switched and sets menu bar items
	private void refreshMenuBar() {
		menuBar.removeAll();
		menuBar.add(fileMenu);
		TextDocumentPanel activeDoc = getActiveDocumentPanel();
		
		if (activeDoc != null) {
			menuBar.add(activeDoc.getEditMenu());
			menuBar.add(activeDoc.getSpellCheckMenu());
		}
	}
}