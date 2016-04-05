package lib.util;

public class WorkerThread extends Thread {
	private final Queue<Runnable> workQueue = new Queue<Runnable>();

	private final String threadName;
	
	public static final WorkerThread backgroundIOThread = new WorkerThread("Background I/O");
	public static final WorkerThread guiActivitiesThread = new WorkerThread("GUI activities");
	public static final WorkerThread networkThread = new WorkerThread("Networking");
	
	static {
		backgroundIOThread.start();
		guiActivitiesThread.start();
		networkThread.start();
	}
	
	public WorkerThread(String name) {
		super(name);
		this.threadName = name;
	}

	public void run() {
		while(true) {
			if(!workQueue.isEmpty()) {
				Runnable task;
				
				synchronized(workQueue) {
					task = workQueue.dequeue();
				}
				System.out.println(threadName + " thread: Starting " + task);
				task.run();
				System.out.println(threadName + " thread: Finished " + task);
			} else {
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void enqueue(Runnable runnable) {
		synchronized(workQueue) {
			workQueue.enqueue(runnable);
		}
	}
}
