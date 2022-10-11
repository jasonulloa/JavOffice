package spellcheck; 

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import wordhelper.SpellChecker;

/* Holds a SpellChecker object that is shared between a config panel and a spellcheck panel
 * Can return its panels in order to be displayed */
public class SpellCheckManager {
	private final SpellChecker spChecker;
	private final SpellCheckPanel spCheckPanel;
	private final ConfigPanel configPanel;
	
	//instantiate components
	{
		spChecker = new SpellChecker();
		spCheckPanel = new SpellCheckPanel(spChecker);
		configPanel = new ConfigPanel(spChecker);
	}
	
	public JPanel getConfigPanel() {
		return configPanel;
	}
	
	public JPanel getSpellCheckPanel() {
		if (!spChecker.hasWordList() || !spChecker.hasKeyboard()) {
			JOptionPane.showMessageDialog(null, "The spellchecker has not been configured.");
			return configPanel;
		}
		
		return spCheckPanel;
	}
}