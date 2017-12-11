/*************************************************************************
 * Name: Artem K. 
 * Email: w1ld at inbox dot ru
 *
 * See http://coursera.cs.princeton.edu/algs4/assignments/collinear.html
 * and http://coursera.cs.princeton.edu/algs4/checklists/collinear.html
 *************************************************************************/


// NEXT (21.10.2014 Tue 20:06) : 
/*
 * why fails:
Note: .\Fast.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
(10000, 0) -> (7000, 3000) -> (3000, 7000) -> (0, 10000)

*/

import java.util.Comparator;

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

	   Point[] points2 = new Point[N];

	   for(int i = 0; i < N; i++) 
	   {
		   Point p0 = points[i];

		   for(int j=i; j < N; j++)
		   {
			   points2[j] = points[j];
		   }

		   //StdOut.println("p0 : " + p0.toString());

		   quick3way(points2, p0.SLOPE_ORDER, i);

		   assert isSorted(points2, p0.SLOPE_ORDER, i);

		   int equalSlopes = 1; 
		   for(int j = i + 1; j < N; j++)
		   {
			   if(p0.SLOPE_ORDER.compare(points2[j - 1], points2[j]) == 0)
			   {
				   equalSlopes++;
				   if(j + 1 == N)
				   {
					   if(equalSlopes > 2) sliceAndPrint(p0, points2, j, equalSlopes);
				   }
			   }
			   else
			   {
				   if(equalSlopes > 2) sliceAndPrint(p0, points2, j - 1, equalSlopes);
				   equalSlopes = 1;
			   }

		   }
	   }
	   //StdOut.println("End - " + args[0]);
   }

   private static void sliceAndPrint(Point first, Point[] points, int last, int n)
   {
	   Point[] slice = new Point[n + 1];
	   slice[0] = first;
	   for(int k = 0; k < n; k++)
	   {
		   slice[k + 1] = points[last - k];
	   }
	   
	   print(slice);
   }

   private static boolean print(Point[] points)
   {
	   // insertion sort:
	   for(int i = 1; i < points.length; i++)
	   {
		   for(int j = i; j > 0 && less(points[j], points[j - 1]); j--)
		   {
			   exch(points, j, j - 1);
		   }
	   }

	   StdOut.print(points[0].toString());
	   for(int i = 1; i < points.length; i++)
	   {
		   StdOut.print( " -> " + points[i].toString());
	   }
	   StdOut.println();

	   points[0].drawTo(points[points.length-1]);
	   return true;
   }

   private static boolean isSorted(Point[] points, Comparator<Point> comparator, int lo)
   {
	   //StdOut.println(" is sorted:");
	   for(int i = lo; i + 1 < points.length; i++)
	   {
		   //StdOut.println(points[i] + " " ) ;

		   // p0.drawTo(points[i]);
		   // p0.drawTo(points[i+1]);
		   if(comparator.compare(points[i], points[i + 1]) > 0)
		   {
			   return false;
		   }
	   }

	   return true;
   }

   private static void shuffle(Point[] a, int lo)
   {
       for(int i=lo + 1; i < a.length; i++)
       {
    	   int j = StdRandom.uniform(lo, i);

    	   exch(a, i, j);
       }
   }
   
   private static void quick3way(Point[] points, Comparator<Point> comparator, int lo)
   {
	   //StdOut.println(" quick3way: lo=" +lo);
	   shuffle(points, lo);
	   //for(int k=0; k<points.length; k++)
	   //{
	   //    StdOut.println(points[k].toString() + " " + p0.slopeTo(points[k]));
	   //}

	   sort(points, comparator, lo, points.length - 1);
   }

   private static void sort(Point[] points, Comparator<Point> comparator, int lo, int hi)
   {
	   // StdOut.printf(" sort(.., .., lo = %d, hi = %d) \n", lo, hi);

	   if(lo >= hi) return;

	   int lt = lo;
	   int gt = hi;
	   int i = lt;
	   Point v = points[lo];

	   while(i <= gt)
	   {
		   int cmp = comparator.compare(points[i], v);
		   //StdOut.printf(" cmp(%s, %s) = %d\n", points[i], v, cmp);
		   // StdOut.printf(" %s compare %s : %s\n", points[i], v, cmp);
		   if(cmp < 0)	exch(points, i++, lt++);
		   else if(cmp > 0) 	exch(points, i, gt--);
		   else			i++;
	   }

	   //StdOut.printf("  v = %s, i = %s, lt = %d, gt = %d \n", v, i, lt, gt);
	   //for(int k=lo; k<=hi; k++)
	   //{
	   //    StdOut.println(points[k].toString() + " " + p0.slopeTo(points[k]));
	   //}


	   sort(points, comparator, lo, lt - 1);
	   sort(points, comparator, gt + 1, hi);
   }

   private static boolean less(Comparable c0, Comparable c1)
   {
	   return c0.compareTo(c1) < 0;
   }

   private static void exch(Point[] a, int i, int j)
   {
	   Point temp = a[i];
	   a[i] = a[j];
	   a[j] = temp;
   }
   
}
