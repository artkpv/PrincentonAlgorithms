// http://coursera.cs.princeton.edu/algs4/assignments/percolation.html

public class PercolationStats {
	double mean = 0;
	double stddev = 0;
	public PercolationStats(int N, int T)    // perform T independent computational experiments on an N-by-N grid
	{
		if(N <= 0) throw new IllegalArgumentException("N");
		if(T <= 0) throw new IllegalArgumentException("T");

		double threshholdSum = 0;
		double deviationSum = 0;
		double[] threshholds = new double[T];
		for(int t=1; t<=T; t++)
		{
			Percolation percolation = new Percolation(N);
			int i,j;
			int count = 0;
			double threshhold = 0;
			
			while(!percolation.percolates())
			{
				do {
					i = StdRandom.uniform(N)+1;
					j = StdRandom.uniform(N)+1;
				} while (percolation.isOpen(i,j));
						
				percolation.open(i,j);
				count++;
			}
			threshhold = (double)count/(N*N);
			threshholdSum += threshhold;
			threshholds[t-1] = threshold;
		}
		mean = threshholdSum/ T;


		for(int i =0; i<threshholds.length; i++)
		{

			// stddev =  // TODO NEXT

		}
	}
	public double mean()                     // sample mean of percolation threshold
	{
		return mean;
	}
	public double stddev()                   // sample standard deviation of percolation threshold
	{
		return stddev;
	}
//	public double confidenceLo()             // returns lower bound of the 95% confidence interval
//	public double confidenceHi()             // returns upper bound of the 95% confidence interval
	public static void main(String[] args)   // test client, described below
	{
		if(args.length != 2) 
			throw new IllegalArgumentException();

        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

		PercolationStats stats = new PercolationStats(N,T);

		StdOut.println("mean =" + stats.mean());
	}
}
