
// NEXT
// http://coursera.cs.princeton.edu/algs4/checklists/8puzzle.html
// http://coursera.cs.princeton.edu/algs4/assignments/8puzzle.html
public class Solver {


	private class SearchNode implements Comparable<SearchNode>
	{
		private int count;
		private Board board;
		private SearchNode previous;

		private SearchNode(Board board, SearchNode previous, int count)
		{
			this.board = board;
			this.count = count;
			this.previous = previous;
		}

		public int compareTo(SearchNode that)
		{
			int thisManhattan = this.board.manhattan() + this.count;

			int thatManhattan = that.board.manhattan() + that.count;

			return thisManhattan - thatManhattan;
		}

		public Iterable<SearchNode> neighbors()
		{
			Stack<SearchNode> neighborNodes = new Stack<SearchNode>();
			for(Board neighbor : board.neighbors())
			{
				SearchNode newNode = new SearchNode(neighbor, this, count+1);
				neighborNodes.push(newNode);
			}

			return neighborNodes;
		}

		public Iterable<Board> path()
		{
			Stack<Board> boardStack = new Stack<Board>();
			
			SearchNode current = this;
			while(current != null)
			{
				boardStack.push(current.board);
				current = current.previous;
			}

			return boardStack;
		}

	}

	private SearchNode solution;

	// find a solution to the initial board (using the A* algorithm)
	public Solver(Board initial)		   
	{
		MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
		MinPQ<SearchNode> pq2 = new MinPQ<SearchNode>();

		pq.insert(new SearchNode(initial, null, 0));
		pq2.insert(new SearchNode(initial.twin(), null, 0));

		SearchNode min;
		SearchNode min2;
		while(true)
		{
			min = pq.delMin();

			if(min.board.isGoal())
			{
				solution = min;
				break;
			}

			min2 = pq2.delMin();
			if(min2.board.isGoal())
			{
				solution = null;
				break;
			}

			for(SearchNode node : min.neighbors())
			{
				if(min.previous == null || !node.board.equals(min.previous.board))
					pq.insert(node);
			}

			for(SearchNode node : min2.neighbors())
			{
				if(min2.previous == null || !node.board.equals(min2.previous.board))
					pq2.insert(node);
			}
		}

	}

	// is the initial board solvable?
	public boolean isSolvable()			
	{
		return solution != null;
	}

	// min number of moves to solve initial board; -1 if unsolvable
	public int moves()					 
	{
		return solution != null ? solution.count : -1;
	}

	// sequence of boards in a shortest solution; null if unsolvable
	public Iterable<Board> solution()	  
	{
		return solution != null ? solution.path() : null;
	}

	public static void main(String[] args) 
	{
		// create initial board from file
		In in = new In(args[0]);
		int N = in.readInt();
		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		Board initial = new Board(blocks);

		// solve the puzzle
		Solver solver = new Solver(initial);

		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}
