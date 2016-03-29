package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;

import data.Paper;
import gui.PaperTrackerWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;

public class PaperBase implements ActionListener, CaretListener, EventHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final DefaultTableModel paperTableModel;
	
	private final HashSet<Paper> paperCollection = new HashSet<Paper>();

	public PaperBase(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		this.paperTableModel = new DefaultTableModel();
		
		paperTableModel.setColumnCount(2);
		paperTableModel.setColumnIdentifiers(new String[]{"Date", "Title"});
		
		window.paperTable.setModel(paperTableModel);
		
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
		paperTableModel.setRowCount(0);
		for(Paper paper : relevantPapers) {
			paperTableModel.addRow(new String[]{paper.publicationDate, paper.title});
		}
	}

	private Paper[] filter(String searchQuery) {
		if(!searchQuery.equals("")) {
			ArrayList<Paper> relevantPapers = new ArrayList<Paper>();
			for(Paper paper : paperCollection) {
				boolean containsTitle = paper.title == null ? false : paper.title.contains(searchQuery);
				boolean containsAbstract = paper.abstractText == null ? false : paper.abstractText.contains(searchQuery);
				boolean containsAuthors = paper.authors == null ? false : paper.authors.contains(searchQuery);
				boolean containsDate = paper.publicationDate == null ? false : paper.publicationDate.contains(searchQuery);
				if(containsTitle || containsAbstract || containsAuthors || containsDate) {
					relevantPapers.add(paper);
				}
			}
			return relevantPapers.toArray(new Paper[relevantPapers.size()]);
		} else {
			return paperCollection.toArray(new Paper[paperCollection.size()]);
		}
		
	}

}
