package core;

import gui.PaperTrackerWindow;
import gui.SwingUtils;

public class PaperTrackerMain {

	public static void main(String[] args) {
		SwingUtils.setSwingSettings();
		PaperTrackerWindow window = new PaperTrackerWindow();
		
		window.setTitle("Paper Tracker");
		window.setVisible(true);
	}

}
