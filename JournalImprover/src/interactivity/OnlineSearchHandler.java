package interactivity;

import gui.PaperImportWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventType;
import lib.util.WorkerThread;
import querying.crossref.CrossRefLoader;
import data.Paper;

public class OnlineSearchHandler implements ActionListener {

	private final PaperImportWindow window;
	private final EventDispatcher eventDispatcher;

	public OnlineSearchHandler(PaperImportWindow window, EventDispatcher eventDispatcher) {
		this.window = window;
		this.eventDispatcher = eventDispatcher;
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
			Paper[] crossRefPapers = CrossRefLoader.query(query, this);
			printStatusMessage("CrossRef returned " + crossRefPapers.length + " papers.");
			eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.IMPORT_PAPERS, crossRefPapers));
		} catch (IOException e1) {
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

}
