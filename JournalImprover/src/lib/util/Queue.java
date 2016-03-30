package lib.util;

import java.util.ArrayList;
import java.util.Iterator;

public class Queue<DataType> implements Iterable<DataType> {
	private final ArrayList<DataType> queue;
	
	public Queue() {
		this.queue = new ArrayList<DataType>();
	}
	
	private Queue(ArrayList<DataType> queue) {
		this.queue = queue;
	}
	
	public void enqueue(DataType object)
	{
		this.queue.add(object);
	}
	
	public DataType dequeue()
	{
		if(this.queue.isEmpty())
		{
			throw new RuntimeException("Queue is empty!");
		}
		return this.queue.remove(0);
	}
	
	public boolean isEmpty()
	{
		return this.queue.isEmpty();
	}

	public int remaining() {
		return queue.size();
	}

	@Override
	public Iterator<DataType> iterator() {
		return new QueueIterator<DataType>(this);
	}

	@SuppressWarnings("unchecked")
	public Queue<DataType> copyOf() {
		return new Queue<DataType>((ArrayList<DataType>) queue.clone());
	}
}
