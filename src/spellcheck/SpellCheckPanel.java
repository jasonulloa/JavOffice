package spellcheck;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.MatchResult;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import wordhelper.SpellChecker;

/* Runs the actual spellcheck by taking in a text component that it scans through
 * Offers suggestions based on SpellChecker and updates text component based on user choice */
public class SpellCheckPanel extends JPanel {
	private static final long serialVersionUID = 2331569311532370131L;
	
	private final SpellChecker spChecker;
	private Scanner scan;
	private MatchResult result;
	private JTextComponent textComponent;
	
	private final JLabel spLabel;
	private final JButton ignoreButton, addButton, changeButton, closeButton;
	private final JComboBox<String> changeOptions;
	
	private int offset;
	
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Spellcheck"));
		
		spLabel = new JLabel(" ") {
			private static final long serialVersionUID = 1L;
			{
				setFont(getFont().deriveFont(16.0f));
			}
			@Override
			public void setText(String text) {
				super.setText("Spelling: " + text);
			}
		};
		
		ignoreButton = new JButton("Ignore");
		ignoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				next();
			}
		});
		
		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					spChecker.addWordToDictionary(textComponent.getText().substring(result.start() + offset, result.end() + offset));
					next();
				}
				catch (IOException ioe) {
					JOptionPane.showMessageDialog(null, "An error occured while attempting to add a word to the wordlist.", "File Error", 
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		changeOptions = new JComboBox<String>();
		changeButton = new JButton("Change");
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				highlightError();
				String choice = changeOptions.getSelectedItem().toString();
				offset += choice.length() - (result.end() - result.start());
				textComponent.setEditable(true);
				textComponent.replaceSelection(choice);
				textComponent.setEditable(false);
				next();
			}
		});
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		
		JPanel titlePanel = new JPanel();
		titlePanel.add(spLabel);
		
		JPanel addIgnorePanel = new JPanel();
		addIgnorePanel.add(ignoreButton);
		addIgnorePanel.add(addButton);
		
		JPanel changePanel = new JPanel();
		changePanel.add(changeOptions);
		changePanel.add(changeButton);
		
		JPanel optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(addIgnorePanel, "North");
		optionsPanel.add(changePanel, "Center");
		
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new BorderLayout());
		footerPanel.add(closeButton, "South");
		
		add(titlePanel);
		add(optionsPanel);
		add(Box.createVerticalGlue());
		add(footerPanel);
		
		offset = 0;
	}
	
	SpellCheckPanel(SpellChecker inSpChecker) {
		spChecker = inSpChecker;
	}
	
	//Starts the spellchecker
	public void runSpellCheck(JTextComponent inTextComponent) {
		textComponent = inTextComponent;
		textComponent.setEditable(false);
		scan = new Scanner(textComponent.getText());
		scan.useDelimiter("([^A-Za-z])");
		offset = 0;
		next();
	}
	
	//Moves to the next word, exits if done
	private void next() {
		String word = null;
		
		while (scan.hasNext() && !spChecker.isSpellingError(word = scan.next())) {  //exit loop on spelling error
			word = null;
		}
		
		if (word == null) {  //no more errors
			close();
			JOptionPane.showMessageDialog(null, "The SpellChecker has completed.");
			return;
		}
		
		word = word.toLowerCase();
		result = scan.match();
		spLabel.setText(word);
		changeOptions.removeAllItems();
		
		for (String option : spChecker.getSpellingSuggestions(word, 10)) {
			changeOptions.addItem(option);
		}
		
		if (changeOptions.getSelectedIndex() == -1) {
			changeButton.setEnabled(false);
		}
		else {
			changeButton.setEnabled(true);
		}
		
		highlightError();
	}
	
	//Selects the spelling error in the text component
	private void highlightError() {
		textComponent.requestFocus();
		textComponent.setCaretPosition(result.start() + offset);
		textComponent.setSelectionStart(result.start() + offset);
		textComponent.setSelectionEnd(result.end() + offset);
	}
	
	//Exits the spellchecker
	private void close() {
		offset = 0;
		textComponent.setEditable(true);
		Container parent = getParent();
		if (parent != null) {
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
		}
	}
}