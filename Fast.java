
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

	   for(int i = 0; i < N; i++) 
	   {
		   Point p0 = points[i];

		   Point[] points2 = new Point[N-1];

		   // TODO NEXT HERE
		   // for(int j = 0; j < N; j++)

		   
		   quick3way(ss);

		   for(int j = 0; j < N - 1; j++)
		   {
			   StdOut.println(sp[0].toString() + " -> " + sp[1].toString()+ " -> " + sp[2].toString()+ " -> " + sp[3].toString());
			   sp[0].drawTo(sp[1]);
			   sp[1].drawTo(sp[2]);
			   sp[2].drawTo(sp[3]);
		   }
	   }
   }

   private static void quick3way(Comparable[] a)
   {
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
