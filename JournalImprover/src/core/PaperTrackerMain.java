package core; 

import gui.PaperTrackerWindow;
import gui.SwingUtils;
import interactivity.CommentsTracker;
import interactivity.IdeaTracker;
import interactivity.OnlinePaperImportHandler;
import interactivity.PaperBase;
import interactivity.PaperDisplayer;
import interactivity.PaperImportHandler;
import lib.events.EventDispatcher;

public class PaperTrackerMain {

	public static void main(String[] args) {
		SwingUtils.setSwingSettings();
		PaperTrackerWindow window = new PaperTrackerWindow();
		EventDispatcher mainDispatcher = new EventDispatcher();
		
		PaperBase paperBase = new PaperBase(window, mainDispatcher);
		new PaperImportHandler(window, mainDispatcher);
		new PaperDisplayer(window, mainDispatcher);
		new IdeaTracker(paperBase, window, mainDispatcher);
		new CommentsTracker(paperBase, window, mainDispatcher);
		new OnlinePaperImportHandler(window, mainDispatcher);
		
		window.setTitle("Paper Tracker");
		window.setVisible(true);
	}

}
