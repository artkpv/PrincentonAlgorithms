import java.util.Iterator;

// http://coursera.cs.princeton.edu/algs4/checklists/8puzzle.html
// http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html

public class Board 
{
	private int N;
	private int[][] blocks;
	private Board previous;

	// construct a board from an N-by-N array of blocks
	// (where blocks[i][j] = block in row i, column j)
	public Board(int[][] blocks)           
	{
		N = blocks.length;
		this.blocks = new int[N][N];
		for(int i=0; i < N; i++)
		{
			for(int j=0; j < N; j++)
			{
				this.blocks[i][j] = blocks[i][j];
			}
		}
		previous = null;
	}

	// board dimension N 
	public int dimension()                
	{
		return N;
	}

	// number of blocks out of place 
	public int hamming()                  
	{
		int hamming = 0;
		for(int i=0; i < N; i++)
		{
			for(int j=0; j < N; j++)
			{
				int block = blocks[i][j];
				if(block != 0 && block != i*N + j + 1) 
					hamming++;
			}
		}
		return hamming;
	}

	// sum of Manhattan distances between blocks and goal 
	public int manhattan()                
	{
		int manhattan = 0;

		for(int i=0; i < N; i++)
		{
			for(int j=0; j < N; j++)
			{
				int block = blocks[i][j];

				if(block != 0)
				{
					int x = (block - 1) % N;
					manhattan += Math.abs(x - j);

					int y = (block - 1) / N ;
					manhattan += Math.abs(y - i);
				}
			}
		}

		return manhattan;
	}
	// is this board the goal board? 
	public boolean isGoal()               
	{
		return hamming() == 0;
	}

	// a boadr that is obtained by exchanging two adjacent blocks in the same row 
	public Board twin()                   
	{ 
		Board twin = new Board(blocks);	
		if(twin.N == 1)
			return twin;

		int previousBlock = -1;

		boolean isTwin = false;
		for(int i = twin.N - 1; i >= 0 && !isTwin; i--)
		{
			for(int j = twin.N - 1; j > 0 && !isTwin; j--)
			{
				if(twin.blocks[i][j] != 0 && twin.blocks[i][j-1] != 0)
				{
					twin.exch(i, j, i, j - 1);
					isTwin = true;
				}
			}
		}
		return twin;
	}

	// does this board equal y? 
	public boolean equals(Object y)       
	{ 
		if(y == null) return false;

		if(y.getClass() != this.getClass()) return false;

		Board that = (Board)y;

		if(this.N != that.N) return false;

		for(int i = 0; i < N; i++)
		{
			for(int j = 0; j < N; j++)
			{
				if(this.blocks[i][j] != that.blocks[i][j]) return false;
			}
		}

		return true;
	}

	// all neighboring boards 
	public Iterable<Board> neighbors()    
	{ 
		return new NeighborsCollection(this); 
	}

	private void exch(int i, int j, int i2, int j2)
	{
		int temp = blocks[i][j];
		blocks[i][j] = blocks[i2][j2];
		blocks[i2][j2] = temp;
	}

	private class NeighborsCollection implements Iterable<Board>
	{
		Board[] neighbors;
		int size;

		public NeighborsCollection(Board board)
		{
			neighbors = new Board[4];
			size = 0;

			for(int i = 0; i < N; i++)
			{
				for(int j = 0; j < N; j++)
				{
					if(board.blocks[i][j] == 0)
					{
						fillNeighbors(board, i - 1, j, i, j);
						fillNeighbors(board, i + 1, j, i, j );
						fillNeighbors(board, i, j - 1, i, j );
						fillNeighbors(board, i, j + 1, i, j );
						return;
					}
				}
			}
		}

		private void fillNeighbors(Board board, int i, int j, int i0, int j0)
		{
			if(0 <= i && i < board.N && 0 <= j && j < board.N) 
			{
				Board neighbor = new Board(board.blocks);
				neighbor.exch(i, j, i0, j0);
				neighbors[size++] = neighbor;
				//StdOut.println(" new neighbor at " + (size - 1) + " : \n" + neighbor);
			}
		}

		public Iterator<Board> iterator() 
		{
			return new NeighborsIterator();
		}

		private class NeighborsIterator implements Iterator<Board>
		{
			private int i;

			public NeighborsIterator()
			{
				i = 0;
			}

			public boolean hasNext()
			{
				return i < size;
			}

			public Board next()
			{
				Board nextRes =  neighbors[i++];
				//StdOut.println(" current: " + (i - 1)  + " \n" + nextRes);
				return nextRes;
			}

			public void remove()
			{
				throw new java.lang.UnsupportedOperationException();
			}
		}


	}

	// string representation of this board (in the output format specified below) 
	public String toString()              
	{ 
		StringBuilder s = new StringBuilder();
		s.append(N + "\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				s.append(String.format("%2d ", blocks[i][j]));
			}
			s.append("\n");
		}
		return s.toString();
	}

	// unit tests (not graded) 
	public static void main(String[] args)
	{
		ShouldGetHamming();
		ShouldGetManhattan();
		ShouldGetGoal();
		ShouldGetEquality();

		ShouldExchange();
		ShouldIterateNeighbors();

		ShouldGetTwin();
	}

	private static void ShouldExchange()
	{
		Board board = new Board(new int[][] {{1, 2}, {3, 0}});
		board.exch(0, 0, 0, 1);
		assert board.equals(new Board(new int[][] {{2, 1}, {3, 0}}));
	}

	private static void ShouldGetHamming()
	{
		assert (new Board(new int[][] {{1, 2}, {3, 0}}).hamming()) == 0;
		assert (new Board(new int[][] {{1, 2}, {0, 3}}).hamming()) == 1;
		assert (new Board(new int[][] {{0}}).hamming()) == 0;
		assert (new Board(new int[][] {{1,2,3}, {4,5,6}, {0,7,8}}).hamming()) == 2;
		assert (new Board(new int[][] {{8,1,3}, {4,0,2}, {7,6,5}}).hamming()) == 5;
	}

	private static void ShouldGetManhattan()
	{
		assert (new Board(new int[][] {{1, 2}, {3, 0}}).manhattan()) == 0;
		assert (new Board(new int[][] {{1, 2}, {0, 3}}).manhattan()) == 1;
		assert (new Board(new int[][] {{0}}).manhattan()) == 0;
		assert (new Board(new int[][] {{1,2,3}, {4,5,6}, {0,7,8}}).manhattan()) == 2;
		assert (new Board(new int[][] {{8,1,3}, {4,0,2}, {7,6,5}}).manhattan()) == 10;
	}

	private static void ShouldGetGoal()
	{
		assert (new Board(new int[][] {{1, 2}, {3, 0}}).isGoal());
		assert (new Board(new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}}).isGoal());
	}

	private static void ShouldGetEquality()
	{
		assert !(new Board(new int[][] {{0}}).equals(new Object()));
	//	assert !(new Board(new int[][] {{0}}).equals(null));
		assert !(new Board(new int[][] {{0}}).equals(new Board(new int[][] {{1}})));
		assert (new Board(new int[][] {{0}}).equals(new Board(new int[][] {{0}})));
		assert (new Board(new int[][] {{0, 1}, {2, 3}}).equals(new Board(new int[][] {{0, 1}, {2, 3}})));
		assert !(new Board(new int[][] {{0, 1}, {2, 3}}).equals(new Board(new int[][] {{0, 1}, {3, 2}})));
	}

	private static void ShouldIterateNeighbors()
	{
		Board origin = new Board(new int[][] {{0, 1}, {2, 3}});
		Board[] neighbors = {
			new Board(new int[][] {{1, 0}, {2, 3}}),
			new Board(new int[][] {{2, 1}, {0, 3}})
		};

		for(int i=0; i < neighbors.length ; i++)
		{ 
			boolean found = false;
			for(Board neighbor : origin.neighbors())
			{
				if(neighbors[i].equals(neighbor))
				{
					found = true;
					break;
				}
			}
			assert found : (" The following neighbor not found \n" + neighbors[i].toString() );
		}
	}

	private static void ShouldGetTwin()
	{
		assert (
				new Board(new int[][] {{0, 1}, {2, 3}})
				.twin()
				.equals(new Board(new int[][] {{0, 1}, {3, 2}}))
			   );
		assert (
				new Board(new int[][] {{0}})
				.twin()
				.equals(new Board(new int[][] {{0}}))
			   );
		assert (
				new Board(new int[][] {{3, 1}, {2, 0}})
				.twin()
				.equals(new Board(new int[][] {{1, 3}, {2, 0}}))
			   );
		assert (
				new Board(new int[][] {{5, 1, 2}, {6 ,7, 8}, {3, 4, 0}})
				.twin()
				.equals(new Board(new int[][] {{5, 1, 2}, {6 ,7, 8},{4, 3, 0}}))
			   );
	}
}
