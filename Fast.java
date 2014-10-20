
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

	   for(int i = 0; i < N; i++) 
	   {
		   Point p0 = points[i];

		   StdOut.println("p0 : " + p0.toString());

		   quick3way(points2, p0.SLOPE_ORDER);

		   assert isSorted(points2, p0.SLOPE_ORDER, p0);

		   int duplicatesNumber = 0;
		   for(int j = 1; j < N; j++)
		   {
			   if( points2[j-1] == points2[j])
			   {
				   duplicatesNumber++;
				   if(duplicatesNumber >= 3) // the p0 and 3 other
				   {
					   StdOut.print(p0.toString());
					   for(int k = 0; k < 4; k++)
					   {
						   Point p1 = points2[j - duplicatesNumber];
						   StdOut.print(" -> " + p1.toString());
					   }
					   StdOut.println();
					   p0.drawTo(points2[j]);
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

   private static boolean isSorted(Point[] points, Comparator<Point> comparator, Point p0)
   {
	   StdOut.println(" is sorted:");
	   for(int i = 0; i + 1 < points.length; i++)
	   {
		   StdOut.println(points[i] + " " ) ;
		   StdOut.println(points[i+1] + " ");
		   StdOut.println(comparator.compare(points[i], points[i + 1]));

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
   
   private static void quick3way(Point[] points, Comparator<Point> comparator)
   {
	   shuffle(points);
	   sort(points, comparator, 0, points.length - 1);
   }

   private static void sort(Point[] points, Comparator<Point> comparator, int lo, int hi)
   {
	   if(lo >= hi) return;

	   int lt = lo, gt = hi;
	   int i = lt;
	   Point v = points[lo];

	   while(i <= gt)
	   {
		   int cmp = comparator.compare(points[i], v);
		   if(cmp < 0)	exch(points, i++, lt++);
		   if(cmp > 0) 	exch(points, i, gt--);
		   else			i++;
	   }

	   sort(points, comparator, lo, lt - 1);
	   sort(points, comparator, gt + 1, hi);
   }

   private static void exch(Point[] a, int i, int j)
   {
	   Point temp = a[i];
	   a[i] = a[j];
	   a[j] = temp;
   }
   
}
