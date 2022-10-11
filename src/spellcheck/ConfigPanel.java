package spellcheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import filechooser.TypeFileChooser;
import wordhelper.SpellChecker;

//Takes in a SpellChecker which it can use to modify the current keyboard and wordlist setup 
public class ConfigPanel extends JPanel {
	private static final long serialVersionUID = 4162203796360479471L;
	
	private final SpellChecker spChecker;
	private final JLabel wordlistLabel, keyboardLabel;
	private final JButton wordlistButton, keyboardButton, closeButton;
	
	//configure the Config Panel
	{
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		setBorder(new TitledBorder("Configure"));
		
		wordlistLabel = new JLabel(" ") {
			private static final long serialVersionUID = 1L;
			@Override
			public void setText(String text) {
				super.setText(".wl: " + text);
			}
		};
		wordlistButton = new JButton("Select Wordlist...");
		wordlistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TypeFileChooser wordlistChooser = new TypeFileChooser("Wordlists (*.wl)", ".wl");
				wordlistChooser.setDialogTitle("Select Wordlist...");
				int returnValue = wordlistChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = wordlistChooser.getSelectedFile();
					if (file.exists()) {
						spChecker.loadWordList(file);
						updateFields();
					}
					else {
						JOptionPane.showMessageDialog(null, file.getName() + " was not found.", "File Not Found", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		keyboardLabel = new JLabel(" ") {
			private static final long serialVersionUID = 1L;
			@Override
			public void setText(String text) {
				super.setText(".kb: " + text);
			}
		};
		keyboardButton = new JButton("Select Keyboard...");
		keyboardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TypeFileChooser keyboardChooser = new TypeFileChooser("Keyboard files (*.kb)", ".kb");
				keyboardChooser.setDialogTitle("Select Keyboard...");
				int returnValue = keyboardChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = keyboardChooser.getSelectedFile();
					if (file.exists()) {
						spChecker.loadKeyboard(file);
						updateFields();
					}
					else {
						JOptionPane.showMessageDialog(null, file.getName() + " was not found.", "File Not Found", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ConfigPanel.this.getParent() != null) {  //'this' points to ActionListener, so need ConfigPanel.this
					ConfigPanel.this.getParent().remove(ConfigPanel.this);
				}
			}
		});
		
		add(wordlistLabel);
		add(wordlistButton);
		add(Box.createVerticalStrut(20));
		add(keyboardLabel);
		add(keyboardButton);
		add(Box.createVerticalGlue());
		add(closeButton);
	}
	
	public ConfigPanel(SpellChecker inSpChecker) {
		spChecker = inSpChecker;
		//loads files in another thread
		new Thread(() -> {
			spChecker.loadWordList(new File("src/wordlist.wl"));
			spChecker.loadKeyboard(new File("src/qwerty-us.kb"));
			updateFields();
		}).start();
	}
	
	//Updates the text labels with the currently selected file names
	private void updateFields() {
		wordlistLabel.setText(spChecker.getFileByType(0).getName());
		keyboardLabel.setText(spChecker.getFileByType(1).getName());
	}
}