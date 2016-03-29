package gui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressWindow {
	
	private final JDialog frame;
	private final JProgressBar progressBar;
	private double progress = 0;
	private final double totalItems;

	public ProgressWindow(PaperTrackerWindow window, int totalItems, String name) {
		this.frame = new JDialog(window, name);
		this.progressBar = new JProgressBar();
		
		frame.setMinimumSize(new Dimension(300, 50));
		frame.setLocation(100, 100);
		frame.setResizable(false);
		frame.add(progressBar);
		frame.setVisible(true);
		
		this.totalItems = totalItems;
	}

	public void destroy() {
		frame.setVisible(false);
	}

	public synchronized void incrementProgress(double amount) {
		progress += amount;
		this.progressBar.setValue((int) ((progress / totalItems) * 100));
	}

}
