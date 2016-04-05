package interactivity.paperBase;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListSelectionModel;
import javax.swing.SwingUtilities;

import backend.Backend;
import data.Comment;
import data.Paper;
import data.Rating;
import gui.PaperTrackerWindow;
import interactivity.PaperTrackerTableModel;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventType;

final class PaperUpdater implements Runnable {

	private final PaperTrackerWindow window;
	private final PaperTrackerTableModel paperTableModel;
	private final Collection<Paper> papers;
	private final EventDispatcher eventDispatcher;
	private final Backend backend;

	public PaperUpdater(PaperTrackerWindow window, Backend backend, PaperTrackerTableModel paperTableModel, EventDispatcher eventDispatcher) {
		this.window = window;
		this.backend = backend;
		this.paperTableModel = paperTableModel;
		this.papers = backend.papers.getAllPapers();
		this.eventDispatcher = eventDispatcher;
	}

	public void run() {
		String searchQuery = window.searchPapersField.getText();
		Paper[] relevantPapers = filter(searchQuery);
		eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.PAPER_BASE_PAPERS_FILTERED, relevantPapers));
		boolean rowCountChanged = relevantPapers.length != paperTableModel.getRowCount();
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				if(rowCountChanged) {
					paperTableModel.setRowCount(0);
				}
				
				for(int row = 0; row < relevantPapers.length; row++) {
					Paper paper = relevantPapers[row];
					Comment comment = backend.comments.getCommentByPaperTitle(paper.title);
					String rating = comment != null ? comment.rating.displayName : Rating.None.displayName;
					if(!rowCountChanged) {
						paperTableModel.setValueAt(paper.publicationDate.toPrettyString(), row, 0);
						paperTableModel.setValueAt(rating, row, 1);
						paperTableModel.setValueAt(paper.title, row, 2);
					} else {
						paperTableModel.addRow(new String[]{paper.publicationDate.toPrettyString(), rating, paper.title});
					}
				}
				
				eventDispatcher.dispatchEvent(new Event<Object>(EventType.PAPER_BASE_PAPER_UPDATE_COMPLETE));
				
				window.revalidate();
			}
		});
	}
	
	private Paper[] filter(String searchQuery) {
		if(!searchQuery.equals("")) {
			ArrayList<Paper> relevantPapers = new ArrayList<Paper>();
			String[] keywords = searchQuery.replace("  ", " ").split(" ");
			for(String keyword : keywords) {
				for(Paper paper : papers) {
					boolean containsTitle = paper.title == null ? false : paper.title.contains(keyword);
					boolean containsAbstract = paper.abstractText == null ? false : paper.abstractText.contains(keyword);
					boolean containsAuthors = paper.authors == null ? false : paper.containsAuthor(keyword);
					boolean containsDate = paper.publicationDate == null ? false : paper.publicationDate.toPrettyString().contains(keyword);
					if(containsTitle || containsAbstract || containsAuthors || containsDate) {
						if(!relevantPapers.contains(paper)) {
							relevantPapers.add(paper);
						}
					}
				}
			}
			return relevantPapers.toArray(new Paper[relevantPapers.size()]);
		} else {
			return papers.toArray(new Paper[papers.size()]);
		}
		
	}
}