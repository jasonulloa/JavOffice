package filechooser;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

//Write once, use many single-file chooser
public class TypeFileChooser extends JFileChooser {
	private static final long serialVersionUID = 4789339188012133655L;
	
	public TypeFileChooser(String inDesc, String inType) {
		setCurrentDirectory(new File(System.getProperty("user.dir")));
		setAcceptAllFileFilterUsed(false);  //Disallows "All Files"
		setFileFilter(new FileFilter(){
			public String getDescription(){
				return inDesc;
			}
			
			public boolean accept(File f){
				if(f.isDirectory()){
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(inType);
				}
			}
		});
	}
}