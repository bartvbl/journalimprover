package backend;

import java.util.ArrayList;
import java.util.Iterator;

import cache.IdeaCache;
import data.Idea;
import lib.util.WorkerThread;

public class IdeaBackend {
	private final ArrayList<Idea> ideaList;

	public IdeaBackend(PaperBackend papers) {
		this.ideaList = IdeaCache.load(papers);
	}

	public int getIdeaCount() {
		return ideaList.size();
	}

	public void addIdea(Idea idea) {
		this.ideaList.add(idea);
	}

	public Idea[] all() {
		return ideaList.toArray(new Idea[ideaList.size()]);
	}

	public Idea getIdeaByIndex(int selectedIndex) {
		if(selectedIndex < 0 || selectedIndex >= ideaList.size()) {
			throw new RuntimeException("Index " + selectedIndex + " is out of bounds!");
		}
		return ideaList.get(selectedIndex);
	}
	
	public void writeCache() {
		WorkerThread.backgroundIOThread.enqueue(new Runnable() {
			@Override
			public void run() {
				IdeaCache.store(ideaList);
			}
		});
	}

	public void removeIdeaByIndex(int index) {
		this.ideaList.remove(index);
	}
}
