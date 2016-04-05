package interactivity.paperBase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import backend.Backend;
import cache.PaperBaseCache;
import data.Paper;
import gui.PaperTrackerWindow;
import gui.ProgressWindow;
import interactivity.PaperTrackerTableModel;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;
import lib.util.WorkerThread;

public class PaperBase implements ActionListener, CaretListener, EventHandler, ListSelectionListener {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	private final Backend backend;
	
	private final PaperTrackerTableModel paperTableModel;
	
	private final DefaultListSelectionModel paperTableSelectionModel;
	
	private Paper[] currentDisplayedPapers = new Paper[0];

	public PaperBase(Backend backend, PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		this.backend = backend;
		
		this.paperTableModel = new PaperTrackerTableModel();
		this.paperTableSelectionModel = new DefaultListSelectionModel();
		
		paperTableModel.setColumnCount(2);
		paperTableModel.setColumnIdentifiers(new String[]{"Date", "Title"});
		
		paperTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		paperTableSelectionModel.addListSelectionListener(this);
		
		window.paperTable.setModel(paperTableModel);
		window.paperTable.setSelectionModel(paperTableSelectionModel);
		
		window.paperTable.setDragEnabled(false);
		window.paperTable.setRowSorter(new TableRowSorter<PaperTrackerTableModel>(paperTableModel));
		
		window.searchPapersField.addCaretListener(this);
		window.addRelevantPaperButton.addActionListener(this);
		
		eventDispatcher.addEventListener(this, EventType.PAPER_BASE_IMPORT_PAPERS);
		eventDispatcher.addEventListener(this, EventType.PAPER_BASE_PAPERS_FILTERED);
		
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
		if(event.eventType == EventType.PAPER_BASE_IMPORT_PAPERS) {
			Paper[] loadedPapers = (Paper[]) event.getEventParameterObject();
			ProgressWindow progressWindow = new ProgressWindow(window, loadedPapers.length, "Import progress");
			for(Paper paper : loadedPapers) {
				backend.papers.registerPaper(paper);
				progressWindow.incrementProgress(1);
			}
			updatePaperList();
			WorkerThread.enqueue(new PaperBaseSaver(backend.papers.getAllPapers()));
			progressWindow.destroy();
		} else if(event.eventType == EventType.PAPER_BASE_PAPERS_FILTERED) {
			Paper[] filteredPapers = (Paper[]) event.getEventParameterObject();
			this.currentDisplayedPapers = filteredPapers;
		}
	}

	private void updatePaperList() {
		WorkerThread.enqueue(new PaperUpdater(this.window, this.paperTableModel, backend.papers.getAllPapers(), eventDispatcher));
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		int index = paperTableSelectionModel.getLeadSelectionIndex();
		if(index < 0 || index >= currentDisplayedPapers.length) {
			return;
		}
		if(index < window.paperTable.getModel().getRowCount()) {
			int modelIndex = window.paperTable.convertRowIndexToModel(index);
			Paper selectedPaper = currentDisplayedPapers[modelIndex];
			eventDispatcher.dispatchEvent(new Event<Paper>(EventType.PAPER_SELECTED, selectedPaper));
		}
	}
}
