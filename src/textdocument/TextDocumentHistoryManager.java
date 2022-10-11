package textdocument;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

//Keeps track of changes to a document, enabling undoing and redoing
public class TextDocumentHistoryManager {
	//The document being tracked
	private Document doc;
	
	//Allows control of undo and redo
	private final UndoManager undoManager;
	private final JMenuItem undoItem;
	private final JMenuItem redoItem;
	
	{
		undoManager = new UndoManager();
		
		undoItem = new JMenuItem("Undo");
		undoItem.setMnemonic('U');
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		undoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					undoManager.undo();
				}
				catch (CannotUndoException cue) {
					JOptionPane.showMessageDialog(null, "Error occured while trying to undo.", "Undo Error", JOptionPane.ERROR_MESSAGE);
				}
				
				updateDocumentHistory();
			}
		});
		
		redoItem = new JMenuItem("Redo");
		redoItem.setMnemonic('R');
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		redoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					undoManager.redo();
				}
				catch (CannotRedoException cre) {
					JOptionPane.showMessageDialog(null, "Error occured while trying to redo.", "Redo Error", JOptionPane.ERROR_MESSAGE);
				}
				
				updateDocumentHistory();
			}
		});
		
		updateDocumentHistory();
	}
	
	//Manager that takes a document to control
	public TextDocumentHistoryManager(Document inDoc) {
		setDocument(inDoc);
	}
	
	//Set the current document to manage
	public void setDocument(Document inDoc) {
		doc = inDoc;
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent uee) {
				undoManager.addEdit(uee.getEdit());
				updateDocumentHistory();
			}
		});
	}
	
	//Updates buttons enabled/disabled status
	private void updateDocumentHistory() {
		undoItem.setEnabled(undoManager.canUndo());
		redoItem.setEnabled(undoManager.canRedo());
	}
	
	public JMenuItem getUndoMenuItem() {
		return undoItem;
	}
	
	public JMenuItem getRedoMenuItem() {
		return redoItem;
	}
}