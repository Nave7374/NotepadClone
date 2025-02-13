package Com.notepad.clone;

import javax.swing.SwingUtilities;

public class NotepadCloneApplication {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(NotepadFrame::new);
	}
}