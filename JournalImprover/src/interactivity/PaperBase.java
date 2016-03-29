package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import data.Paper;
import gui.PaperTrackerWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;

public class PaperBase implements ActionListener, CaretListener, EventHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final DefaultListModel<String> paperListModel;
	private final HashSet<Paper> paperCollection = new HashSet<Paper>();

	public PaperBase(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		this.paperListModel = new DefaultListModel<String>();
		window.knownPapersList.setModel(paperListModel);
		
		window.searchPapersField.addCaretListener(this);
		window.addRelevantPaperButton.addActionListener(this);
		
		eventDispatcher.addEventListener(this, EventType.IMPORT_PAPER);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
	}

	@Override
	public void caretUpdate(CaretEvent event) {
		this.updatePaperList();
	}

	@Override
	public void handleEvent(Event<?> event) {
		if(event.eventType == EventType.IMPORT_PAPER) {
			Paper paper = (Paper) event.getEventParameterObject();
			if(!paperCollection.contains(paper)) {
				paperCollection.add(paper);
				updatePaperList();
			}
		}
	}

	private void updatePaperList() {
		String searchQuery = window.searchPapersField.getText();
		Paper[] relevantPapers = filter(searchQuery);
		paperListModel.clear();
		for(Paper paper : relevantPapers) {
			paperListModel.addElement(paper.title);
		}
	}

	private Paper[] filter(String searchQuery) {
		if(!searchQuery.equals("")) {
			ArrayList<Paper> relevantPapers = new ArrayList<Paper>();
			for(Paper paper : paperCollection) {
				if(paper.title.contains(searchQuery)) {
					relevantPapers.add(paper);
				}
			}
			return relevantPapers.toArray(new Paper[relevantPapers.size()]);
		} else {
			return paperCollection.toArray(new Paper[paperCollection.size()]);
		}
		
	}

}
