package interactivity.paperBase;

import java.util.Collection;

import cache.PaperBaseCache;
import data.Paper;

final class PaperBaseSaver implements Runnable {
	
	private Collection<Paper> paperCollection;

	public PaperBaseSaver(Collection<Paper> collection) {
		this.paperCollection = collection;
	}

	public void run() {
		PaperBaseCache.store(paperCollection);
	}
}