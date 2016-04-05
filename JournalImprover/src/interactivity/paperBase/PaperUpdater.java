package interactivity.paperBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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
			final int threadCount = 12;

			CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);
			ArrayList<Paper> allFoundPapers = new ArrayList<Paper>();

			String[] keywords = searchQuery.replace("  ", " ").split(" ");
			Paper[] allPapers = papers.toArray(new Paper[papers.size()]);
			
			int papersPerThread = papers.size() / threadCount;

			for(int thread = 0; thread < threadCount; thread++) {
				new PaperSearchThread(this, keywords, allPapers, papersPerThread, barrier, thread, allFoundPapers).start();
			}
			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
			return allFoundPapers.toArray(new Paper[allFoundPapers.size()]);
		} else {
			return papers.toArray(new Paper[papers.size()]);
		}

	}
}