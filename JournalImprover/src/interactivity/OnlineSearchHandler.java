package interactivity;

import gui.PaperImportWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import lib.util.WorkerThread;
import querying.crossref.CrossRefLoader;
import data.Paper;

public class OnlineSearchHandler implements ActionListener {

	private final PaperImportWindow window;

	public OnlineSearchHandler(PaperImportWindow window) {
		this.window = window;
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
			Paper[] crossRefPapers = CrossRefLoader.query(query);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		window.searchButton.setEnabled(true);
		window.searchPapersField.setEnabled(true);
	}

}
