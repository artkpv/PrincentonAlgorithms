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

// TODO NEXT : SLOPE_ORDER

import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER;       // YOUR DEFINITION HERE

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

	private class SlopeOrderComparator implements Comparator<Point> {
		private Point p0;

		public SlopeOrderComparator(Point point){
			p0 = point;
		}
		
		public int compare(Point p1, Point p2) {
			double slope1 = p0.slopeTo(p1);
			double slope2 = p0.slopeTo(p2);
			return Double.compare(slope1, slope2);
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
        if(this.y < that.y || this.x < that.x) return -1;
		if(this.y > that.y || this.x > that.x) return 1;
		return 0;
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
			assert new Point(2, 2).compareTo(new Point(1, 1)) > 0;
			StdOut.println(" SUCCESS test 1");
		}

		public static void ShouldCalculateSlope() {
			assert new Point(1, 1).slopeTo(new Point(2, 2)) == 1;
			assert new Point(1, 1).slopeTo(new Point(2, 1)) == 0;
			assert new Point(1, 1).slopeTo(new Point(1, 2)) == Double.POSITIVE_INFINITY;
			assert new Point(1, 1).slopeTo(new Point(1, 1)) == Double.NEGATIVE_INFINITY;
			StdOut.println(" SUCCESS test 2");
		}

		public static void ShouldCompareBySlopes() {
			Comparator<Point> c = new Point(1, 1).SLOPE_ORDER;
			assert c.compare(new Point(2, 2), new Point(3, 3)) == 0;
			assert c.compare(new Point(2, 2), new Point(2, 3)) < 0;
			assert c.compare(new Point(2, 2), new Point(3, 2)) > 0;
			assert c.compare(new Point(2, 1), new Point(2, 2)) < 0;
			assert c.compare(new Point(1, 2), new Point(2, 2)) > 0;
			StdOut.println(" Success test 3" );
		}

	}
}
