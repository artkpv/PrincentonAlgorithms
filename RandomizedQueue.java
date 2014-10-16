import java.util.Iterator;
import java.util.NoSuchElementException;
/*
 *
 * Next (06.10.2014 Mon 23:58) : 
 *  - use resizable array
 *  - shuffle before dequeue() and isShuffled = false on enqueue()
 *
 */
public class RandomizedQueue<Item> implements Iterable<Item> {
	private Item[] a;
	private int N;
	private boolean isShuffled;

	// construct an empty randomized queue
	public RandomizedQueue()
	{
		a = (Item[]) new Object[2];
		N = 0;
		isShuffled = false;
	}
	
    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {
		private int current;
		private Item[] shuffledA;

        public ListIterator() {
			shuffledA = a;
			shuffle(shuffledA);
            current = 0;
        }

        public boolean hasNext()  { return current < N;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return shuffledA[current++];
        }
    }


	private void resize(int size)
	{
		assert N <= size;
		Item[] newArray = (Item[]) new Object[size];
		for(int i=0; i<N;i++)
		{
			newArray[i] = a[i];
		}
		a = newArray;
	}

	private void shuffle(Item[] myA)
	{
		int r;
		for(int i=0; i<N; i++)
		{
			r = StdRandom.uniform(i, N);
			Item temp = myA[r];
			myA[r] = myA[i];
			myA[i] = temp;
		}
		isShuffled = true;
	}
	
	// is the queue empty?
	public boolean isEmpty()
	{
		return N == 0;
	}

	// return the number of items on the queue
	public int size()
	{
		return N;
	}

	// add the item
	public void enqueue(Item item)
	{
		if(item == null) throw new NullPointerException ("item");

		if(a.length == N) resize(N*2);
		a[N] = item;	
		N++;
		isShuffled = false;
	}

	// delete and return a random item
	public Item dequeue()
	{
		if(N == 0)
			throw new java.util.NoSuchElementException("Deque is empty!");
		if(a.length/4 == N) resize(a.length/2);
		if(!isShuffled)
			shuffle(a);
		Item item = a[N - 1];
		a[N - 1] = null;
		N--;
		return item;
	}

	// return (but do not delete) a random item
	public Item sample()
	{
		if(N == 0)
			throw new java.util.NoSuchElementException("Deque is empty!");
		return a[StdRandom.uniform(0, N)];
	}

	// return an independent iterator over items in random order
	public Iterator<Item> iterator()
	{
		return new ListIterator();
	}

	// unit testing
	public static void main(String[] args) 
	{
		StdOut.println("Testing:");
		RandomizedQueueTests.ShouldBeEmptyAfterFullEnqueueAndDequeue();
		RandomizedQueueTests.ShouldDequeueAtRandom();
		RandomizedQueueTests.ShouldCreatedIndependentIterators();
	}

	private static class RandomizedQueueTests 
	{
		public static void ShouldBeEmptyAfterFullEnqueueAndDequeue()
		{
			RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();
			int N = 10000;
			for(int i = 0; i<N; i++)
			{
				q.enqueue(i);
			}

			for(int i = 0; i<N; i++)
			{
				q.dequeue();
			}

			assert q.size() == 0;
			StdOut.println(" SUCCESS ShouldBeEmptyAfterFullEnqueueAndDequeue");
		}

		public static void ShouldDequeueAtRandom()
		{
			int N =10;
			RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();
			for(int i = 0; i<N; i++)
			{
				q.enqueue(i + 1);
			}

			int[] r = new int[N];
			for(int i = 0; i<N; i++)
			{
				int item = q.dequeue();
				assert item != 0;
				r[i] = item;
			}

			assert q.size() == 0;

			for(int i = 0; i<N; i++)
			{
				q.enqueue(i + 1);
			}

			boolean isRandom = false;
			for(int i = 0; i<N; i++)
			{
				int item = q.dequeue();
				if(r[i] != item)
				{
					isRandom = true;
				}
			}

			assert q.size() == 0;
			assert isRandom;

			StdOut.println(" SUCCESS ShouldDequeueAtRandom");
		}

		public static void ShouldCreatedIndependentIterators()
		{
			int N =10;
			RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();
			for(int i = 0; i<N; i++)
			{
				q.enqueue(i + 1);
			}

			boolean isShuffled = false;
			int i=0;
			for(int item : q)
			{
				int j=0;
				for(int item2 : q)
				{
					j++;
					if(i == j)
					{
						if(item == item2)
						{
							isShuffled = true;
						}
						break;
					}


				}

				i++;
			}
			assert isShuffled;
			
			StdOut.println(" SUCCESS should create independent iterators");
		}
	}
}
