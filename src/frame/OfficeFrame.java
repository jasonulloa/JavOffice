package frame;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import textdocument.TextDocumentManager;

public class OfficeFrame extends JFrame {
	private static final long serialVersionUID = 9183816558021947333L;
	
	//configures the Text Editor
	{
		setTitle("JavOffice");
		setSize(800,600);
		setLocation(200, 100);
		setJMenuBar(new JMenuBar());
		getContentPane().add(new TextDocumentManager(getJMenuBar()));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);  //close with X in the top-right corner
	}
	
	//creates the Text Editor
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		} finally {
			SwingUtilities.invokeLater(() -> {
				new OfficeFrame().setVisible(true);
			});
		}
	}
}