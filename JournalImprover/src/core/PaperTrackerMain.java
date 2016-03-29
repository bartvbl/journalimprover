package core;

import gui.PaperTrackerWindow;
import gui.SwingUtils;
import lib.events.EventDispatcher;

public class PaperTrackerMain {

	public static void main(String[] args) {
		SwingUtils.setSwingSettings();
		PaperTrackerWindow window = new PaperTrackerWindow();
		EventDispatcher mainDispatcher = new EventDispatcher();
		
		new PaperBase(window, mainDispatcher);
		
		window.setTitle("Paper Tracker");
		window.setVisible(true);
	}

}
