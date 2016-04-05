package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import backend.Backend;
import data.Paper;
import gui.PaperTrackerWindow;
import gui.ProgressWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventType;
import lib.util.WorkerThread;

public class PaperImportHandler implements ActionListener {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	private final Backend backend;

	public PaperImportHandler(Backend backend, PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		this.backend = backend;
		
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
			WorkerThread.enqueue(new Runnable() {
				public void run() {
					Paper[] loadedPapers = PaperLoader.loadPapers(chosenFile, window);
					eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.PAPER_BASE_IMPORT_PAPERS, loadedPapers));
				}
			});
		}
	}

}
