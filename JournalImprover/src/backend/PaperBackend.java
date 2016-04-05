package backend;

import java.util.Collection;
import java.util.HashMap;

import cache.PaperBaseCache;
import data.Paper;

public class PaperBackend {
	private final HashMap<String, Paper> paperCollection;
	
	public PaperBackend() {
		paperCollection = PaperBaseCache.load();
	}

	public void registerPaper(Paper paper) {
		if(!paperCollection.containsKey(paper.title)) {
			paperCollection.put(paper.title, paper);
		} else {
			paperCollection.get(paper.title).update(paper);
		}		
	}

	public Collection<Paper> getAllPapers() {
		return paperCollection.values();
	}

	public Paper getPaperByTitle(String paperTitle) {
		return paperCollection.get(paperTitle);
	}
}
