package core; 

import backend.Backend;
import gui.PaperTrackerWindow;
import gui.SwingUtils;
import interactivity.CommentsTracker;
import interactivity.IdeaTracker;
import interactivity.OnlinePaperImportHandler;
import interactivity.PaperDisplayer;
import interactivity.PaperImportHandler;
import interactivity.paperBase.PaperBase;
import lib.events.EventDispatcher;

public class PaperTrackerMain {

	public static void main(String[] args) {
		SwingUtils.setSwingSettings();
		PaperTrackerWindow window = new PaperTrackerWindow();
		EventDispatcher mainDispatcher = new EventDispatcher();
		
		Backend backend = new Backend();
		
		new PaperBase(backend, window, mainDispatcher);
		new PaperImportHandler(backend, window, mainDispatcher);
		new PaperDisplayer(backend, window, mainDispatcher);
		new IdeaTracker(backend, window, mainDispatcher);
		new CommentsTracker(backend, window, mainDispatcher);
		new OnlinePaperImportHandler(backend, window, mainDispatcher);
		
		window.setTitle("Paper Tracker");
		window.setVisible(true);
	}

}
