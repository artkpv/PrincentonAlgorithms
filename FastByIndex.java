
public class Fast {
   public static void main(String[] args)
   {
	   if(args.length != 1) throw new IllegalArgumentException();

	   In in = new In(args[0]);
	   
	   int N = in.readInt();

	   StdDraw.setXscale(0, 32768);
	   StdDraw.setYscale(0, 32768);

	   Point[] points = new Point[N];
	   for(int i = 0; i < N; i++)
	   {
		   int x = in.readInt();
		   int y = in.readInt();

		   Point newP = new Point(x, y);
		   points[i] = newP;
		   newP.draw();
		   //StdOut.println(newP);
	   }

	   assert points.length == N;

	   int[] inxs = new int[N];
	   double[] slopes = new double[N];

	   for(int i = 0; i < N; i++) 
	   {
		   Point p0 = points[i];

		   for(int j = 0; j < N; j++) 
		   {
			   inxs[j] = j;
			   slopes[j] = p0.slopeTo(points[j]);
		   }

		   assert slopes[inxs[i]] == Double.NEGATIVE_INFINITY;

		   quick3way(inxs, slopes);

		   StdOut.println(" p0: " + p0.toString());
		   for(int j = 0; j < N; j++) 
		   {
			   StdOut.println(points[inxs[j]].toString());
		   }

		   // TODO NEXT: find out why not sorted!
		   assert isSorted(inxs, slopes);

		   int duplicatesNumber = 0;
		   for(int j = 1; j < N; j++)
		   {
			   if(slopes[inxs[j-1]] == slopes[inxs[j]])
			   {
				   duplicatesNumber++;
				   if(duplicatesNumber >= 3) // the p0 and 3 other
				   {
					   StdOut.print(p0.toString());
					   for(int k = 0; k < 4; k++)
					   {
						   Point p1 = points[inxs[j - duplicatesNumber]];
						   StdOut.print(" -> " + p1.toString());
					   }
					   StdOut.println();
					   p0.drawTo(points[inxs[j]]);
				   }
			   }
			   else
			   {
				   duplicatesNumber = 0;
			   }
		   }
	   }
	   StdOut.println("End");
   }

   private static boolean isSorted(int[] inxs, double[] slopes)
   {
	   StdOut.println(" is sorted:");
	   StdOut.println(slopes[inxs[0]]);
	   for(int i = 1; i < inxs.length; i++)
	   {
		   StdOut.println(slopes[inxs[i]]);
		   if(Double.compare(slopes[inxs[i-1]], slopes[inxs[i]]) > 0)
		   {
			   StdOut.println(Double.compare(slopes[inxs[i-1]], slopes[inxs[i]]));
			   return false;
		   }
	   }

	   return true;
   }

   private static void shuffle(int[] a)
   {
       for(int i=1; i < a.length; i++)
       {
    	   int j = StdRandom.uniform(0, i);
    	   exch(a, i, j);
       }
   }
   
   private static void quick3way(int[] inxs, double[] slopes)
   {
	   shuffle(inxs);
	   sort(inxs, slopes, 0, inxs.length - 1);
   }

   private static void sort(int[] inxs, double[] slopes, int lo, int hi)
   {
	   if(lo >= hi) return;

	   int lt = lo, gt = hi;
	   int i = lt;
	   double v = slopes[inxs[lo]];

	   while(i <= gt)
	   {
		   int cmp = Double.compare(slopes[inxs[i]], v);
		   if(cmp < 0)	exch(inxs, i++, lt++);
		   if(cmp > 0) 	exch(inxs, i, gt--);
		   else			i++;
	   }

	   sort(inxs, slopes, lo, lt - 1);
	   sort(inxs, slopes, gt + 1, hi);
   }

//   private static boolean less(Comparable c0, Comparable c1)
//   {
//	   return c0.compareTo(c1) < 0;
//   }

   private static void exch(int[] a, int i, int j)
   {
	   int temp = a[i];
	   a[i] = a[j];
	   a[j] = temp;
   }
   
}
