package interactivity.paperBase;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import data.Paper;

final class PaperSearchThread extends Thread {
	/**
	 * 
	 */
	private final PaperUpdater PaperSearchThread;
	private final String[] keywords;
	private final Paper[] allPapers;
	private final int papersPerThread;
	private final CyclicBarrier barrier;
	private final int thread;
	private final ArrayList<Paper> allFoundPapers;

	public PaperSearchThread(PaperUpdater paperUpdater, String[] keywords, Paper[] allPapers, int papersPerThread, CyclicBarrier barrier, int thread, ArrayList<Paper> allFoundPapers) {
		PaperSearchThread = paperUpdater;
		this.keywords = keywords;
		this.allPapers = allPapers;
		this.papersPerThread = papersPerThread;
		this.barrier = barrier;
		this.thread = thread;
		this.allFoundPapers = allFoundPapers;
	}

	public void run() {
		ArrayList<Paper> relevantPapers = new ArrayList<Paper>();
		for(int i = thread * papersPerThread; i < papersPerThread * (thread + 1); i++) {
			Paper paper = allPapers[i];
			for(String keyword : keywords) {
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
		synchronized(allFoundPapers) {
			allFoundPapers.addAll(relevantPapers);
		}
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}