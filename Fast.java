/*************************************************************************
 * Name: Artem K. 
 * Email: w1ld at inbox dot ru
 *
 * See http://coursera.cs.princeton.edu/algs4/assignments/collinear.html
 * and http://coursera.cs.princeton.edu/algs4/checklists/collinear.html
 *************************************************************************/


// NEXT (21.10.2014 Tue 13:13) : Fix up duplicates as follows? But fails on timing? 
/*
 *
C:\coding\princeton\algorithms1 [master +0 ~3 -0]> .\javac.cmd .\Fast.java ; .\java.cmd Fast .\collinear\input8.txt
Note: .\Fast.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
(10000, 0) -> (7000, 3000) -> (3000, 7000) -> (0, 10000)
(10000, 0) -> (7000, 3000) -> (3000, 7000) -> (0, 10000)
(10000, 0) -> (7000, 3000) -> (3000, 7000) -> (0, 10000)
(10000, 0) -> (7000, 3000) -> (3000, 7000) -> (0, 10000)
(3000, 4000) -> (6000, 7000) -> (14000, 15000) -> (20000, 21000)
(3000, 4000) -> (6000, 7000) -> (14000, 15000) -> (20000, 21000)
(3000, 4000) -> (6000, 7000) -> (14000, 15000) -> (20000, 21000)


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
	   Point[] points2 = new Point[N];
	   for(int i = 0; i < N; i++)
	   {
		   int x = in.readInt();
		   int y = in.readInt();

		   Point newP = new Point(x, y);
		   points[i] = newP;
		   points2[i] = newP;
		   newP.draw();
		   //StdOut.println(newP);
	   }

	   assert points.length == N && points2.length == N;

	   Point[] startPoints = new Point[N];
	   int startPointsN = 0;

	   for(int i = 0; i < N; i++) 
	   {
		   Point p0 = points[i];

		   //StdOut.println("p0 : " + p0.toString());

		   quick3way(points2, p0.SLOPE_ORDER, p0);

		   assert isSorted(points2, p0.SLOPE_ORDER, p0);

		   int equalSlopesNumber = 1;
		   for(int j = 0; j + 1 < N; j++)
		   {
			   //StdOut.println(points2[j].toString() + ": " + p0.slopeTo(points2[j]));

			   if(p0.SLOPE_ORDER.compare(points2[j], points2[j + 1]) == 0)
			   {
				   equalSlopesNumber++;
			   }
			   else
			   {
				   if(equalSlopesNumber > 2)
				   {
					   Point[] slice = new Point[equalSlopesNumber + 1];
					   slice[0] = p0;
					   for(int k = 0; k < equalSlopesNumber; k++)
					   {
						   int lastInx = j;
						   int offset = lastInx - k;
						   slice[k + 1] = points2[offset];
					   }
					   
					   if(print(slice, startPoints, startPointsN))
					   {
						   startPointsN += 2;
					   }
				   }

				   equalSlopesNumber = 1;
			   }
		   }
	   }
	   //StdOut.println("End - " + args[0]);
   }

   private static boolean print(Point[] points, Point[] startPoints, int startPointsN)
   {
	   // insertion sort:
	   for(int i = 1; i < points.length; i++)
	   {
		   for(int j = i; j > 0 && less(points[j], points[j - 1]); j--)
		   {
			   exch(points, j, j - 1);
		   }
	   }

	   // caching to solve duplicates problem?
	   for(int i=0; i < startPointsN; i++)
	   {
		   if(startPoints[i].compareTo(points[0]) == 0 &&
				   startPoints[i+1].compareTo(points[points.length- 1]) == 0)
			   return false;
	   }

	   startPoints[startPointsN] = points[0];
	   startPoints[startPointsN+1] = points[points.length - 1];
	   
	   StdOut.print(points[0].toString());
	   for(int i = 1; i < points.length; i++)
	   {
		   StdOut.print( " -> " + points[i].toString());
	   }
	   StdOut.println();

	   points[0].drawTo(points[points.length-1]);
	   return true;
   }

   private static boolean isSorted(Point[] points, Comparator<Point> comparator, Point p0)
   {
	   //StdOut.println(" is sorted:");
	   for(int i = 0; i + 1 < points.length; i++)
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

   private static void shuffle(Point[] a)
   {
       for(int i=1; i < a.length; i++)
       {
    	   int j = StdRandom.uniform(0, i);
    	   exch(a, i, j);
       }
   }
   
   private static void quick3way(Point[] points, Comparator<Point> comparator, Point p0)
   {
	   shuffle(points);
	   //for(int k=0; k<points.length; k++)
	   //{
	   //    StdOut.println(points[k].toString() + " " + p0.slopeTo(points[k]));
	   //}

	   sort(points, comparator, 0, points.length - 1, p0);
   }

   private static void sort(Point[] points, Comparator<Point> comparator, int lo, int hi, Point p0)
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


	   sort(points, comparator, lo, lt - 1, p0);
	   sort(points, comparator, gt + 1, hi, p0);
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
