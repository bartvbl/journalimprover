package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import backend.Backend;
import gui.PaperImportWindow;
import gui.PaperTrackerWindow;
import lib.events.EventDispatcher;

public class OnlinePaperImportHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	private final Backend backend;

	public OnlinePaperImportHandler(Backend backend, PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.backend = backend;
		this.eventDispatcher = mainDispatcher;
		
		window.searchOnlineMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PaperImportWindow window = new PaperImportWindow();
				window.setTitle("Import from Online Databases");
				window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				window.progressTextArea.setEditable(false);
				window.entryCountLabel.setText("");
				
				window.setVisible(true);
				
				window.importButton.setEnabled(false);
				
				window.searchButton.addActionListener(new OnlineSearchHandler(window, eventDispatcher));
			}
		});
	}

}
