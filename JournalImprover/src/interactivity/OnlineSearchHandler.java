package interactivity;

import gui.PaperImportWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventType;
import lib.util.WorkerThread;
import nu.xom.ParsingException;
import querying.DataSource;
import querying.crossref.CrossRefLoader;
import querying.ieeexplore.IEEEXPloreLoader;
import querying.scienceDirect.ScienceDirectLoader;
import querying.scopus.ScopusLoader;
import querying.springer.SpringerLoader;
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

		for(DataSource source : DataSource.values()) {
			try {
				printStatusMessage(source.name() + ": Querying..");
				Method[] methods = source.loaderClass.getMethods();
				for(Method method : methods) {
					if(method.getName().equals("query")) {
						Paper[] crossRefPapers = (Paper[]) method.invoke(null, new Object[]{query, this});
						printStatusMessage(source.name() + " returned " + crossRefPapers.length + " papers.");
						eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.PAPER_BASE_IMPORT_PAPERS, crossRefPapers));
						printStatusMessage(source.name() + " papers imported.");				
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				printStatusMessage("Failed to load from " + source.name());
			}
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
