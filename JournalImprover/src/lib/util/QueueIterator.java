package lib.util;

import java.util.Iterator;

public class QueueIterator<DataType> implements Iterator<DataType> {

	private final Queue<DataType> queue;

	public QueueIterator(Queue<DataType> queue) {
		this.queue = queue;
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public DataType next() {
		return queue.dequeue();
	}

}
