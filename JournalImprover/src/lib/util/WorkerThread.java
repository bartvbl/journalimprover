package lib.util;

public class WorkerThread extends Thread {
	private final Runnable runnable;
	
	public WorkerThread(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void run() {
		runnable.run();
	}
}
