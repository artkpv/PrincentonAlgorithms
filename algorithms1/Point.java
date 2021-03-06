/*************************************************************************
 * Name: Artem K. 
 * Email: w1ld at inbox dot ru
 *
 * Compilation:  javac Point.java
 * Execution:
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 *
 * See http://coursera.cs.princeton.edu/algs4/assignments/collinear.html
 * and http://coursera.cs.princeton.edu/algs4/checklists/collinear.html
 *************************************************************************/

import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER;       

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

	private class SlopeOrderComparator implements Comparator<Point> {
		private Point p0;

		public SlopeOrderComparator(Point point){
			p0 = point;
		}
		
		public int compare(Point p1, Point p2) {
			return ((Double)p0.slopeTo(p1)).compareTo(p0.slopeTo(p2));
		}
	}
    // create the point (x, y)
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
		SLOPE_ORDER = new SlopeOrderComparator(this);
    }

    // plot this point to standard drawing
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point that) {
		double subX =  (that.x - this.x);
		double subY =  (that.y - this.y);

		if(subX == 0 && subY == 0) return Double.NEGATIVE_INFINITY;
		if(subX == 0) return Double.POSITIVE_INFINITY;
		if(subY == 0) return 0;
        return subY / subX;
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    public int compareTo(Point that) {

		if(this.y == that.y && this.x == that.x) return 0;

        if(this.y < that.y || (this.y == that.y && this.x < that.x)) return -1;

		return 1;
    }

    // return string representation of this point
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    // unit test
    public static void main(String[] args) {
		PointTests.ShouldCompareByCoordinates();
		PointTests.ShouldCalculateSlope();
		PointTests.ShouldCompareBySlopes();
    }

	private static class PointTests {

		public static void ShouldCompareByCoordinates() {
			assert new Point(1, 1).compareTo(new Point(1, 2)) < 0;
			assert new Point(1, 1).compareTo(new Point(2, 1)) < 0;
			assert new Point(1, 1).compareTo(new Point(2, 2)) < 0;
			assert new Point(1, 1).compareTo(new Point(1, 1)) == 0;
			assert new Point(1, 2).compareTo(new Point(1, 1)) > 0;
			assert new Point(2, 1).compareTo(new Point(1, 1)) > 0;

			// reflexive
			assert new Point(490, 280).compareTo(new Point(208, 319)) < 0;
			assert new Point(208, 319).compareTo(new Point(490, 280)) > 0;
			StdOut.println(" SUCCESS test 1");
		}

		public static void ShouldCalculateSlope() {
			assert new Point(1, 1).slopeTo(new Point(2, 2)) == 1;
			assert new Point(1, 1).slopeTo(new Point(2, 1)) == 0;
			assert new Point(1, 1).slopeTo(new Point(1, 2)) == Double.POSITIVE_INFINITY;
			assert new Point(1, 1).slopeTo(new Point(1, 1)) == Double.NEGATIVE_INFINITY;

			assert new Point(10000, 0).slopeTo(new Point(8000, 2000)) == -1;
			assert new Point(10000, 0).slopeTo(new Point(13000, 0)) == 0;

			StdOut.println(" SUCCESS test 2");
		}

		public static void ShouldCompareBySlopes() {
			Comparator<Point> c = new Point(1, 1).SLOPE_ORDER;
			assert c.compare(new Point(2, 2), new Point(3, 3)) == 0;
			assert c.compare(new Point(2, 2), new Point(2, 3)) < 0;
			assert c.compare(new Point(2, 2), new Point(3, 2)) > 0;
			assert c.compare(new Point(2, 1), new Point(2, 2)) < 0;
			assert c.compare(new Point(1, 2), new Point(2, 2)) > 0;


			assert c.compare(new Point(2, 2), new Point(-3, -3)) == 0;
			assert c.compare(new Point(2, 2), new Point(-2, -3)) < 0;

			assert c.compare(new Point(2, 2), new Point(-2, 2)) > 0;
			assert c.compare(new Point(2, 2), new Point(2, -2)) > 0;

			assert c.compare(new Point(2, 2), new Point(2, 2)) == 0;
			assert c.compare(new Point(1, 1), new Point(1, 1)) == 0;

			assert new Point(10000, 0).SLOPE_ORDER.compare(new Point(13000, 0), new Point(8000, 2000)) > 0;
			// x1, y1 : 0
			// x2, y2 : 2000 - 0 / 8000 - 10000 = -1

			assert new Point(18000, 10000).SLOPE_ORDER.compare(new Point(32000, 10000), new Point(1234, 5678)) < 0;

			StdOut.println(" Success test 3" );
		}

	}
}
