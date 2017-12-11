
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
	private Node first;
	private Node last;
	private int N;

	private class Node
	{
		public Node next;
		public Node previous;
		public Item item;
	}

	// construct an empty deque
	public Deque()                           
	{
		first = null;
		last = null;
		N = 0;
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

		Node newFirst = new Node();
		newFirst.item = item;
		newFirst.previous = null;

		newFirst.next = first;
		if(first != null) first.previous = newFirst;
		first = newFirst;
		N++;

		if(N == 1)
		{
			last = first;
		}

		assert last != null;
		assert first != null;
		assert N > 0;
	}

	// insert the item at the end
	public void addLast(Item item)           
	{
		if(item == null) throw new NullPointerException ("item");

		Node newLast = new Node();
		newLast.item = item;
		newLast.next = null;

		newLast.previous = last;
		if(last != null) last.next = newLast;
		last = newLast;
		N++;

		if(N == 1)
		{
			first = last;
		}

		assert last != null;
		assert first != null;
		assert N > 0;
	}

	// delete and return the item at the front
	public Item removeFirst()                
	{
		if(N == 0)
			throw new java.util.NoSuchElementException("Deque is empty!");

		Item oldItem = first.item;

		if (N == 1)
		{
			first = null;
			last = null;
		}
		else 
		{
			first = first.next;
			first.previous = null;
		}

		N--;
		return oldItem;
	}

	// delete and return the item at the end
	public Item removeLast()                 
	{
		if(N == 0)
			throw new java.util.NoSuchElementException("Deque is empty!");

		Item oldItem = last.item;

		if(N == 1)
		{
			first = null;
			last = null;
		}
		else 
		{
			last = last.previous;
			last.next = null;
		}

		N--;
		return oldItem;
	}

	// return an iterator over items in order from front to end
	public Iterator<Item> iterator()         
	{
		return new ListIterator(first);
	}

   // an iterator, doesn't implement remove() since it's optional
    private class ListIterator implements Iterator<Item> {
		private Node current;

        public ListIterator(Node first) {
            current = first;
        }

        public boolean hasNext()  { return current != null; }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
			Item item = current.item;
			current = current.next;
            return item;
        }
    }

	// unit testing
	public static void main(String[] args)   
	{
		DequeTests.ShouldGetEmptyOnAddingAndRemovingFromDifEnds();

		return;

		/*
		Deque<String> deque = new Deque<String>();
		while (!StdIn.isEmpty()) {
			String item = StdIn.readString();
			//if(item == null || item.isEmpty() || item.trim().isEmpty())
			//	break;

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

			StdOut.println("array:");
			for (String s : deque) {
				StdOut.print(" " + s);
			}
		}
		*/
	}

	private static class DequeTests 
	{
		public static void ShouldGetEmptyOnAddingAndRemovingFromDifEnds()
		{
			int N = 100;
			Deque<String> d = new Deque<String>();
			for(int i = 0; i<N; i++)
			{
				d.addLast(new Integer(i).toString());
			}

			for(int i = 0; i<N; i++)
			{
				assert d != null;
				d.removeFirst();
			}

			assert d.isEmpty();
			assert d.size() == 0;

			StdOut.println(" SUCCESS ShouldGetEmptyOnAddingAndRemovingFromDifEnds");
		}
	}
}
