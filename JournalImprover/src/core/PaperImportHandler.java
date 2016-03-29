package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import gui.PaperTrackerWindow;
import lib.events.EventDispatcher;

public class PaperImportHandler implements ActionListener {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;

	public PaperImportHandler(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		window.importPaperHTMLItem.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(".").getAbsoluteFile());
		fileChooser.setDialogTitle("Open exported HTML file");
		int result = fileChooser.showOpenDialog(window);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			File chosenFile = fileChooser.getSelectedFile();
			Paper[] loadedPapers = PaperLoader.loadPapers(chosenFile, window);
		}
	}

}
