package core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import gui.PaperTrackerWindow;
import lib.events.EventDispatcher;

public class PaperBase implements ActionListener, CaretListener {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final DefaultListModel<String> paperListModel;

	public PaperBase(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		this.paperListModel = new DefaultListModel<String>();
		window.knownPapersList.setModel(paperListModel);
		
		window.searchPapersField.addCaretListener(this);
		window.addRelevantPaperButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
	}

	@Override
	public void caretUpdate(CaretEvent event) {
	}

}
