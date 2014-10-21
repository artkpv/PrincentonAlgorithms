
public class Brute {
   public static void main(String[] args) {
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

	   for(int i = 0; i < N; i++) 
	   {
		   for(int j = i + 1; j < N; j++) 
		   {
			   for(int k = j + 1; k < N; k++) 
			   {
				   for(int p = k + 1; p < N; p++)
				   {
					   Point p0 = points[i], p1 = points[j], p2 = points[k], p3 = points[p];
					   double slope0 = p0.slopeTo(p1);
					   double slope1 = p0.slopeTo(p2);
					   double slope2 = p0.slopeTo(p3);

					   if(slope0 == slope1 && slope1 == slope2)
					   {
						   //StdOut.println(" slopes: " + slope0 + ", " + slope1 + ", " + slope2);
						   Point[] sp = new Point[4];
						   sp[0] = points[i];
						   sp[1] = points[j];
						   sp[2] = points[k];
						   sp[3] = points[p];

						   // insertion sort:
						   for(int r0 = 1; r0 < 4; r0++)
						   {
							   for(int r1 = r0; r1 > 0 && less(sp[r1], sp[r1 - 1]); r1--)
							   {
								   exch(sp, r1, r1 - 1);
							   }
						   }

						   StdOut.println(sp[0].toString() + " -> " + sp[1].toString()+ " -> " + sp[2].toString()+ " -> " + sp[3].toString());
						   sp[0].drawTo(sp[3]);
					   }
				   }
			   }
		   }
	   }
   }

   private static boolean less(Comparable c0, Comparable c1)
   {
	   return c0.compareTo(c1) < 0;
   }

   private static void exch(Object[] a, int i, int j)
   {
	   Object temp = a[i];
	   a[i] = a[j];
	   a[j] = temp;
   }
}
