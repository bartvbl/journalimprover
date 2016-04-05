package backend;

import java.util.HashMap;

import cache.CommentCache;
import data.Comment;
import lib.util.WorkerThread;

public class CommentBackend {
	private final HashMap<String, Comment> commentMap = CommentCache.load();

	public CommentBackend() {
	}

	public Comment getCommentByPaperTitle(String title) {
		return commentMap.get(title);
	}

	public void writeCache() {
		WorkerThread.enqueue(new Runnable() {
			@Override
			public void run() {
				CommentCache.write(commentMap);
			}
		});
	}

	public void addCommentByPaperTitle(String title, Comment newComment) {
		commentMap.put(title, newComment);
	}
}
