import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

	/*
	 * |xxxxxxxxxxxxxxxxxxxxxxx|
	 *     |               | 
	 *     first           last
	 *
	 * N = last - first + 1
	 *
	 */
	private Item[] d;

	private int first, N;

	// construct an empty deque
	public Deque()                           
	{
		d = (Item[]) new Object[4];
		first = 1;
		N = 0;
	}

	private void resize(int newLength, boolean toRight)
	{
		Item[] newD = (Item[]) new Object[newLength];
		if(toRight)
		{
			assert N + first <= newLength;
			for(int i = 0; i <N; i++)
			{
				newD[i + first] = d[i + first];
			}
		}
		else 
		{
			int newFirst = first + newLength - d.length;
			assert N + newFirst <= newLength;
			for(int i = 0; i <N; i++)
			{
				newD[i + newFirst] = d[i + first];
			}
			first = newFirst;
		}
		d = newD;
	}

	// is the deque empty?
	public boolean isEmpty()                 
	{
		return N == 0;
	}

	// return the number of items on the deque
	public int size()                        
	{
		return N;
	}
	// insert the item at the front
	public void addFirst(Item item)          
	{
		if(item == null) throw new NullPointerException ("item");

		if(first == 0) resize(N*2, false);
		d[--first] = item;
		N++;
	}

	// insert the item at the end
	public void addLast(Item item)           
	{
		if(item == null) throw new NullPointerException ("item");

		if(N + first == d.length - 1) resize(first + N*2, true);
		d[N+first] = item;
		N++;
	}

	// delete and return the item at the front
	public Item removeFirst()                
	{
		if(N == 0)
			throw new java.util.NoSuchElementException("Deque is empty!");
		if(first == N*2) resize(d.length - first + N, false);
		Item oldFirst = d[first];
		d[first] = null;
		N--;
		first++;
		return oldFirst;
	}

	// delete and return the item at the end
	public Item removeLast()                 
	{
		if(N == 0)
			throw new java.util.NoSuchElementException("Deque is empty!");
		if(d.length - N - first - 1 == N*2) resize(first + 2*N, true);
		Item oldLast = d[N + first];
		d[N + first] = null;
		N--;
		return oldLast;
	}

	// return an iterator over items in order from front to end
	public Iterator<Item> iterator()         
	{
		return new ListIterator(first);
	}

   // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {
		private int current;

        public ListIterator(int first) {
            current = first;
        }

        public boolean hasNext()  { return current < N + first;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return d[current++];
        }
    }

	// unit testing
	public static void main(String[] args)   
	{
		Deque<String> deque = new Deque<String>();
		while (!StdIn.isEmpty()) {
			String item = StdIn.readString();
			if(item == null || item.isEmpty() || item.trim().isEmpty())
				break;

			if(item.startsWith("-f"))
			{
				StdOut.println(deque.removeFirst());
			}
			else if(item.startsWith("-l"))
			{
				StdOut.println(deque.removeLast());
			}
			else if(item.startsWith("+"))
			{
				deque.addFirst(item);
			}
			else
			{
				deque.addLast(item);
			}
					
			StdOut.println("size of deque = " + deque.size());
		}
		for (String s : deque) {
			StdOut.println(s);
		}

	}
}
