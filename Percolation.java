/*************************************************************************
 *  Author:     Artem K., w1ld at inbox dot ru
 *  Project:    Princenton, Algorithms, part 1. 
 *  Created at: Sep, 2014
 *
 *  
 *
 *  http://coursera.cs.princeton.edu/algs4/assignments/percolation.html
 *
 *************************************************************************/

// TODO: NOT SOLVED. See another file with solution using algorithm inside the Percolation class.

public class Percolation {

	/*
	 * 0 1 2
	 * 3 4 5
	 * 6 7 8 9 10 11
	 *
	 * 0,0 = 0*3+0 = 0
	 * 1,2 = 1*3+2 = 5
	 * 2,2 = 2*3+2 = 8
	 *
	 * N, i, j: id = i*N + j
	 * i = floor(id/N)
	 * j = id%N
	 */

	private int N_;

	private int idTop;
	private int idBottom;
	private int idClosed;

	// create N-by-N grid, with all sites blocked
	public Percolation(int N)                
	{
		throw new UnsupportedOperationException();

		if (N <= 0)
		{
			throw new IllegalArgumentException ("N");
		}
		int power = N*N;
		WeightedQuickUnionUF uf = new WeightedQuickUnionUF(power + 3);
		N_ = N;
		idTop = power;
		idBottom = power+1;
		idOpen = power+2;
	}

	private int getInx(int i, int j)
	{
		if (i < 1) return idTop;
		else if (i > N_) return idBottom;
		else if (j < 1 || j > N_) return idClosed;
		return (i-1)*N_+j-1;
	}

	private int root(int inx)
	{
		int root = id[inx];
		while (root != inx)
		{
			return root(root);
		}
		return root;
	}

	private void union(int p, int q) 
	{
		int rootP = root(p);
		int rootQ = root(q);

		if (rootP == rootQ) return;

		if (rootP == idClosed) return;

		if (rootQ == idClosed) return;

		// make smaller root point to larger one
		if   (sz[rootP] < sz[rootQ]) 
		{
			id[rootP] = rootQ;
			sz[rootQ] += sz[rootP]; 
		}
		else                         
		{
			id[rootQ] = rootP;
			sz[rootP] += sz[rootQ]; 
		}
	}

	private void union(int i, int j, int i2, int j2)
	{
		int inx1 = getInx(i, j);
		int inx2 = getInx(i2, j2);
		union(inx1, inx2);
	}

	private void check(int i, int j)
	{
		if (i < 1 || i > N_) 
		{
			throw new IndexOutOfBoundsException("i");
		}
		if (j < 1 || j > N_) 
		{
			throw new IndexOutOfBoundsException("j");
		}
	}

	private boolean isOpen(int inx)
	{
		return root(inx) != idClosed;
	}

	// open site (row i, column j) if it is not already
	public void open(int i, int j)           
	{
		check(i, j);

		int inx = getInx(i, j);
		if (isOpen(inx)) return;

		id[inx] = inx;
		sz[inx] = 1;

		union(i - 1, j, i, j); // with top cell
		union(i + 1, j, i, j); // with bottom
		union(i, j - 1, i, j); // with left
		union(i, j + 1, i, j); // with right
	}

	public boolean isOpen(int i, int j)      // is site (row i, column j) open?
	{
		check(i, j);
		int inx = getInx(i, j);
		return isOpen(inx);
	}

	public boolean isFull(int i, int j)      // is site (row i, column j) full?
	{
		check(i, j);
		int inx = getInx(i, j);
		return root(inx) == root(idTop);
	}

	public boolean percolates()              // does the system percolate?
	{
		return root(idTop) == root(idBottom);
	}

	private void print()
	{
		for (int i = 0; i < id.length; i++)
		{
			if (i > 0 && i % (N_) == 0)
				StdOut.print("\n" + id[i] + "  ");
			else
				StdOut.print(id[i] + "  ");
		}

		StdOut.print("\n");
	}

	public static void main(String[] args)   // test client, optional
	{
		int N = StdIn.readInt();
		Percolation perc = new Percolation(N);
		perc.print();

		while (!StdIn.isEmpty()) 
		{
			int i = StdIn.readInt();
			int j = StdIn.readInt();
			perc.open(i, j);
			StdOut.println("("+i + "," + j + ")");
			perc.print();
			if (perc.percolates()) StdOut.println("percolates!");
		}
		StdOut.println("end");
	}
}

