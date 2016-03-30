package lib.util;

public class WorkerThread extends Thread {
	private final Queue<Runnable> workQueue = new Queue<Runnable>();
	
	private static final WorkerThread thread = new WorkerThread();
	static {
		thread.start();
	}
	
	public void run() {
		while(true) {
			if(!workQueue.isEmpty()) {
				Runnable task;
				synchronized(workQueue) {
					task = workQueue.dequeue();
				}
				task.run();
			} else {
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void enqueue(Runnable runnable) {
		synchronized(thread.workQueue) {
			thread.workQueue.enqueue(runnable);
		}
	}
}
