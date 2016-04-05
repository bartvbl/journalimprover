package backend;

import java.util.HashMap;

import cache.PaperBaseCache;
import data.Paper;

public class Backend {
	public final PaperBackend papers = new PaperBackend();
	public final IdeaBackend ideas = new IdeaBackend(papers);
	public final CommentBackend comments = new CommentBackend();
}
