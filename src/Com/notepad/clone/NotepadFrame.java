package Com.notepad.clone;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.undo.UndoManager;

public class NotepadFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6698607154906654019L;
	
	private JMenuBar menubar;
	JTextArea txt;
	JScrollPane scrollpane ;
	JFileChooser filechooser ;
	String OriginalContent="";
	boolean saved = false;
	UndoManager undomanager = new UndoManager();
	
	JMenuItem undoitem ;
	JMenuItem redoitem ;
	JMenuItem cutitem ;
	JMenuItem copyitem ;
	JMenuItem selectallitem ;
	JMenuItem pasteitem ;
	
	NotepadFrame(){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(this,e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		this.setTitle("Notepad");
		this.setIconImage(new ImageIcon("D:/Java_eclipse/Programs/src/Com/jframe/projects/icons8-notepad-94.png").getImage());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setLocationByPlatform(false);
		this.setSize(900,600);
		this.setLocationRelativeTo(null);
		//Menu Bar
		
		menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu Format = new JMenu("Format");
		JMenu help = new JMenu("Help");
		
		//file menu items
		JMenuItem newitem = new JMenuItem("New");
		JMenuItem newWindowitem = new JMenuItem("New Window");
		JMenuItem loaditem = new JMenuItem("Open");
		JMenuItem saveitem = new JMenuItem("Save");
		JMenuItem exititem = new JMenuItem("Exit");
		
		newitem.addActionListener(e -> newFile());
		newWindowitem.addActionListener(e -> new newwindowThread());
		loaditem.addActionListener(e -> loadFile());
		saveitem.addActionListener(e -> SaveFile());
		exititem.addActionListener(e -> this.dispose());
		
		file.add(newitem);
		file.add(newWindowitem);
		file.add(loaditem);
		file.add(saveitem);
		file.addSeparator();
		file.add(exititem);
		
		//Edit Menu Items
		undoitem = new JMenuItem("Undo");
		redoitem = new JMenuItem("Redo");
		cutitem = new JMenuItem("Cut");
		copyitem = new JMenuItem("Copy");
		selectallitem = new JMenuItem("SelectAll");
		pasteitem = new JMenuItem("Paste");
		
		undoitem.addActionListener(e -> undo());
		redoitem.addActionListener(e -> redo());
		copyitem.addActionListener(e -> copy());
		pasteitem.addActionListener(e -> paste());
		cutitem.addActionListener(e -> cut());
		selectallitem.addActionListener(e -> selectall());
		
//		edit.add(undoitem);
//		edit.add(redoitem);
//		edit.addSeparator();
		edit.add(cutitem);
		edit.add(copyitem);
		edit.add(pasteitem);
		edit.addSeparator();
		edit.add(selectallitem);
		
		//Format Menu items
		JMenuItem fontitem = new JMenuItem("Font");
		JMenuItem wwitem = new JMenuItem("World Wrap");
		
		Format.add(wwitem);
		Format.add(fontitem);
		
		//Help Menu Items
		JMenuItem about = new JMenuItem("About Editor");
		
		about.addActionListener(e -> aboutwindow());
		
		help.add(about);
		
		file.setMnemonic(KeyEvent.VK_F);
		edit.setMnemonic(KeyEvent.VK_E);
		help.setMnemonic(KeyEvent.VK_H);

		newitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK));
		newWindowitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		loaditem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK));
		saveitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK));
		
		undoitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_DOWN_MASK));
		redoitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,InputEvent.CTRL_DOWN_MASK));
		cutitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_DOWN_MASK));
		copyitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_DOWN_MASK));
		pasteitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_DOWN_MASK));
		selectallitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_DOWN_MASK));
		
		menubar.add(file);
		menubar.add(edit);
		menubar.add(Format);
		menubar.add(help);
		
		this.setJMenuBar(menubar);
		
		// Text area
		
		txt = new JTextArea();
		txt.setFont(new Font("Consolas", Font.PLAIN,14 ));
		scrollpane = new JScrollPane(txt);
		scrollpane.setBorder(BorderFactory.createEmptyBorder());
		filechooser = new JFileChooser();
		filechooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt) ", "txt"));
		filechooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		this.add(scrollpane,BorderLayout.CENTER);
		
		// Window Listener
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
//				OriginalContent = txt.getText();
				SaveToExit();
			}
		});
		
		txt.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				Updatemenuitems();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				Updatemenuitems();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				Updatemenuitems();
			}
		});
		
		Updatemenuitems();		
		this.setVisible(true);
	}
	private void aboutwindow() {
		
		String message = "<html><h2>Notepad Application</h2>" +
                "<p><strong>Version:</strong> 1.0</p>" +
                "<p><strong>Author:</strong> Note Pad</p>" +
                "<p>This is a simple text editor application built with Java.</p></html>";
		
		JOptionPane.showMessageDialog(this, message,"About",JOptionPane.INFORMATION_MESSAGE); 
		
	}
	private void Updatemenuitems() {
		if(txt.getText().isEmpty()) {
			undoitem.setEnabled(false);
			redoitem.setEnabled(false);
			cutitem.setEnabled(false);
			copyitem.setEnabled(false);
			selectallitem.setEnabled(false);
		}
		else {
			undoitem.setEnabled(true);
			redoitem.setEnabled(true);
			cutitem.setEnabled(true);
			copyitem.setEnabled(true);
			selectallitem.setEnabled(true);
		}
	}

	private void selectall() {
		txt.selectAll();
		Updatemenuitems();
	}

	private void cut() {
		txt.cut();
		Updatemenuitems();
	}

	private void paste() {
		txt.paste();
		Updatemenuitems();
	}

	private void copy() {
		txt.copy();
		Updatemenuitems();
	}

	private void redo() {
		if(txt.getText().length()>0) {
			try {
				if(undomanager.canRedo()) {
					undomanager.redo();
				}
			}catch(Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void undo() {
		if(txt.getText().length()>0) {
			try {
				if(undomanager.canUndo()) {
					undomanager.undo();
				}
			}catch(Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadFile() {
		int result = filechooser.showOpenDialog(this);
		if(result == filechooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			try(BufferedReader reader = new BufferedReader(new FileReader(file))){
				txt.read(reader, null);
				OriginalContent = txt.getText();
			}catch(IOException e) {
				JOptionPane.showMessageDialog(this,"Error Opening The File: "+e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void SaveFile() {
		int result = filechooser.showSaveDialog(this);
		if(result == filechooser.APPROVE_OPTION) {
			File file = filechooser.getSelectedFile();
			if(!file.getName().endsWith(".txt")) {
				file = new File(file.getAbsolutePath()+".txt");
			}
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
				txt.write(writer);
				OriginalContent=txt.getText();
				saved=true;
			}catch(IOException e) {
				JOptionPane.showMessageDialog(this, "Error Saving File: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void newFile() {
		if(!OriginalContent.equals("")) {
			int confirm = JOptionPane.showConfirmDialog(this, "Do You Want To Save The Current File?","New File" ,JOptionPane.YES_NO_CANCEL_OPTION );
			if(confirm == JOptionPane.YES_OPTION) {
				try {
					SaveFile();
				}catch(Exception e) {
					JOptionPane.showMessageDialog(this, "Error Saving File: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
			else if(confirm == JOptionPane.CANCEL_OPTION) return;
			txt.setText("");
			OriginalContent="";
		}
		else {
			try {
				SaveFile();
			}catch(Exception e) {
				JOptionPane.showMessageDialog(this, "Error Saving File: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void SaveToExit() {
		if(!txt.getText().equals(OriginalContent)) {
			int confirm = JOptionPane.showConfirmDialog(this, "You have Unsaved Changes, Do you want to save before exit?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
			if(confirm == JOptionPane.YES_OPTION) {
				SaveFile();
				if(!saved) return;
				this.dispose();
			}
			else if(confirm  == JOptionPane.NO_OPTION) {
				this.dispose();
			}
			else if(confirm  == JOptionPane.CANCEL_OPTION);
			// Cancel Does Noting
		}
		else this.dispose();
	}
}

class newwindowThread implements Runnable{

	public newwindowThread() {
		Thread t = new Thread(this);
		t.setDaemon(false);
		t.start();
	}
	
	@Override
	public void run() {
		SwingUtilities.invokeLater(NotepadFrame::new);
	}
}
