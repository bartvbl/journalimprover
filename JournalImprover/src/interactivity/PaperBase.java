package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import cache.PaperBaseCache;
import data.Paper;
import gui.PaperTrackerWindow;
import gui.ProgressWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;
import lib.util.WorkerThread;

public class PaperBase implements ActionListener, CaretListener, EventHandler, ListSelectionListener {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final DefaultTableModel paperTableModel;
	
	private final HashMap<String, Paper> paperCollection;
	private final DefaultListSelectionModel paperTableSelectionModel;
	
	private Paper[] currentDisplayedPapers = new Paper[0];

	public PaperBase(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		this.paperTableModel = new DefaultTableModel();
		this.paperTableSelectionModel = new DefaultListSelectionModel();
		
		paperTableModel.setColumnCount(2);
		paperTableModel.setColumnIdentifiers(new String[]{"Date", "Title"});
		
		paperTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		paperTableSelectionModel.addListSelectionListener(this);
		
		window.paperTable.setModel(paperTableModel);
		window.paperTable.setSelectionModel(paperTableSelectionModel);
		
		window.searchPapersField.addCaretListener(this);
		window.addRelevantPaperButton.addActionListener(this);
		
		eventDispatcher.addEventListener(this, EventType.IMPORT_PAPERS);
		
		paperCollection = PaperBaseCache.load();
		
		updatePaperList();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		int index = paperTableSelectionModel.getLeadSelectionIndex();
		if(index < 0 || index >= currentDisplayedPapers.length) {
			return;
		}
		Paper selectedPaper = currentDisplayedPapers[index];
		eventDispatcher.dispatchEvent(new Event<Paper>(EventType.ADD_RELEVANT_PAPER, selectedPaper));
	}

	@Override
	public void caretUpdate(CaretEvent event) {
		this.updatePaperList();
	}

	@Override
	public void handleEvent(Event<?> event) {
		if(event.eventType == EventType.IMPORT_PAPERS) {
			Paper[] loadedPapers = (Paper[]) event.getEventParameterObject();
			ProgressWindow progressWindow = new ProgressWindow(window, loadedPapers.length, "Import progress");
			for(Paper paper : loadedPapers) {
				if(!paperCollection.containsKey(paper.title)) {
					paperCollection.put(paper.title, paper);
					updatePaperList();
				}
				progressWindow.incrementProgress(1);
			}
			WorkerThread.enqueue(new Runnable() {
				public void run() {
					PaperBaseCache.store(paperCollection.values());
				}
			});
			progressWindow.destroy();
		}
	}

	private void updatePaperList() {
		WorkerThread.enqueue(new Runnable() {
			public void run() {
				String searchQuery = window.searchPapersField.getText();
				Paper[] relevantPapers = filter(searchQuery);
				currentDisplayedPapers = relevantPapers;
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						paperTableModel.setRowCount(0);
						
						for(Paper paper : relevantPapers) {
							paperTableModel.addRow(new String[]{paper.publicationDate, paper.title});
						}
					}
				});
			}
		});
	}

	private Paper[] filter(String searchQuery) {
		if(!searchQuery.equals("")) {
			ArrayList<Paper> relevantPapers = new ArrayList<Paper>();
			for(Paper paper : paperCollection.values()) {
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
			return paperCollection.entrySet().toArray(new Paper[paperCollection.size()]);
		}
		
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		int index = paperTableSelectionModel.getLeadSelectionIndex();
		if(index < 0 || index >= currentDisplayedPapers.length) {
			return;
		}
		Paper selectedPaper = currentDisplayedPapers[index];
		eventDispatcher.dispatchEvent(new Event<Paper>(EventType.PAPER_SELECTED, selectedPaper));
	}

	public Paper getPaperByTitle(String paperTitle) {
		return paperCollection.get(paperTitle);
	}

}
