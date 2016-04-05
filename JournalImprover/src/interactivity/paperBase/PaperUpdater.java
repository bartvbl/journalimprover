package interactivity.paperBase;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.SwingUtilities;

import data.Paper;
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

	public PaperUpdater(PaperTrackerWindow window, PaperTrackerTableModel paperTableModel, Collection<Paper> papers, EventDispatcher eventDispatcher) {
		this.window = window;
		this.paperTableModel = paperTableModel;
		this.papers = papers;
		this.eventDispatcher = eventDispatcher;
	}

	public void run() {
		String searchQuery = window.searchPapersField.getText();
		Paper[] relevantPapers = filter(searchQuery);
		eventDispatcher.dispatchEvent(new Event<Paper[]>(EventType.PAPER_BASE_PAPERS_FILTERED, relevantPapers));
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				paperTableModel.setRowCount(0);
				
				for(Paper paper : relevantPapers) {
					paperTableModel.addRow(new String[]{paper.publicationDate.toPrettyString(), paper.title});
				}
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