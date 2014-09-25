// http://coursera.cs.princeton.edu/algs4/assignments/percolation.html

public class PercolationStats {
	private double mean = 0;
	private double stddev = 0;
	private double confidenceHi = 0;
	private double confidenceLo = 0;
	public PercolationStats(int N, int T)    // perform T independent computational experiments on an N-by-N grid
	{
		if(N <= 0) throw new IllegalArgumentException("N");
		if(T <= 0) throw new IllegalArgumentException("T");

		double thresholdSum = 0;
		double deviationSum = 0;
		double[] thresholds = new double[T];
		for(int t=1; t<=T; t++)
		{
			Percolation percolation = new Percolation(N);
			int i,j;
			int count = 0;
			double threshold = 0;
			
			while(!percolation.percolates())
			{
				do {
					i = StdRandom.uniform(N)+1;
					j = StdRandom.uniform(N)+1;
				} while (percolation.isOpen(i,j));
						
				percolation.open(i,j);
				count++;
			}
			threshold = (double)count/(N*N);
			thresholdSum += threshold;
			thresholds[t-1] = threshold;
		}
		mean = thresholdSum/ T;

		double sharpnessSquareSum = 0;
		for(int i =0; i<thresholds.length; i++)
		{
			sharpnessSquareSum += Math.pow(thresholds[i] - mean, 2);
		}

		stddev = Math.pow(sharpnessSquareSum/(T-1),0.5 );
		confidenceLo = mean - 1.96*stddev/Math.pow(T, 0.5);
		confidenceHi = mean + 1.96*stddev/Math.pow(T, 0.5);
	}
	public double mean()  // sample mean of percolation threshold
	{
		return mean;
	}
	public double stddev()  // sample standard deviation of percolation threshold
	{
		return stddev;
	}
	public double confidenceLo() // returns lower bound of the 95% confidence interval
	{
		return confidenceLo;
	}
	public double confidenceHi() // returns upper bound of the 95% confidence interval
	{
		return confidenceHi;
	}
	public static void main(String[] args)   // test client, described below
	{
		if(args.length != 2) 
			throw new IllegalArgumentException();

        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

		PercolationStats stats = new PercolationStats(N,T);

		StdOut.println("mean                     = " + stats.mean());
		StdOut.println("stddev                   = " + stats.stddev());
		StdOut.println("95% confidence interval  = " + stats.confidenceLo() + ", "+ stats.confidenceHi());
	}
}
