package interactivity;

import gui.PaperImportWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventType;
import lib.util.WorkerThread;
import nu.xom.ParsingException;
import querying.crossref.CrossRefLoader;
import querying.ieeexplore.IEEEXPloreLoader;
import querying.scienceDirect.ScienceDirectLoader;
import data.Paper;

public class OnlineSearchHandler implements ActionListener {

	private final PaperImportWindow window;
	private final EventDispatcher eventDispatcher;

	public OnlineSearchHandler(PaperImportWindow window, EventDispatcher eventDispatcher) {
		this.window = window;
		this.eventDispatcher = eventDispatcher;
		window.progressBar.setMinimum(0);
		window.progressBar.setMaximum(100);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.searchButton.setEnabled(false);
		window.searchPapersField.setEnabled(false);
		
		WorkerThread.enqueue(new Runnable() {
			@Override
			public void run() {
				performQuerying();
			}
		});
	}

	protected void performQuerying() {
		String query = window.searchPapersField.getText();
		
		try {
			printStatusMessage("CrossRef: Querying..");
			Paper[] crossRefPapers = CrossRefLoader.query(query, this);
			printStatusMessage("CrossRef returned " + crossRefPapers.length + " papers.");
			eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.IMPORT_PAPERS, crossRefPapers));
			printStatusMessage("CrossRef papers imported.");
			
			printStatusMessage("IEEEXPlore: Querying..");
			Paper[] ieeexplorePapers = IEEEXPloreLoader.query(query, this);
			printStatusMessage("IEEEXPlore: returned " + ieeexplorePapers.length + " papers.");
			eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.IMPORT_PAPERS, ieeexplorePapers));
			printStatusMessage("IEEEXplore: papers imported.");
			
			printStatusMessage("ScienceDirect: Querying..");
			Paper[] scienceDirectPapers = ScienceDirectLoader.query(query, this);
			printStatusMessage("ScienceDirect: returned " + scienceDirectPapers.length + " papers.");
			eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.IMPORT_PAPERS, scienceDirectPapers));
			printStatusMessage("ScienceDirect: papers imported.");
			
			printStatusMessage("Scopus: Querying..");
			Paper[] scopusPapers = ScienceDirectLoader.query(query, this);
			printStatusMessage("Scopus: returned " + scopusPapers.length + " papers.");
			eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.IMPORT_PAPERS, scopusPapers));
			printStatusMessage("Scopus: papers imported.");
		} catch (IOException | ParsingException e1) {
			e1.printStackTrace();
		}
		
		window.searchButton.setEnabled(true);
		window.searchPapersField.setEnabled(true);
	}

	public void printStatusMessage(String message) {
		String currentText = window.progressTextArea.getText();
		currentText += message + "\n";
		window.progressTextArea.setText(currentText);
	}

	public void setProgress(double progress) {
		window.progressBar.setValue((int) (progress * 100.0));
		
	}

}
