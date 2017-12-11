public class PointSET 
{
	private SET<Point2D> set;

	// construct an empty set of points 
	public PointSET() 
	{
		set = new SET<Point2D>();
	}

	// is the set empty? 
	public boolean isEmpty() 
	{
		return set.size() == 0;
	}

	// number of points in the set 
	public int size() 
	{
		return set.size();
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) 
	{
		set.add(p);
	}

	// does the set contain point p? 
	public boolean contains(Point2D p) 
	{
		return set.contains(p);
	}

	// draw all points to standard draw 
	public void draw() 
	{
		for(Point2D p : set)
		{
			p.draw();
		}
	}

	// all points that are inside the rectangle 
	public Iterable<Point2D> range(RectHV rect) 
	{
		Stack<Point2D> insiders = new Stack<Point2D>();
		for(Point2D p : set)
		{
			if(rect.distanceSquaredTo(p) == 0)
			{
				insiders.push(p);
			}
		}

		return insiders;
	}

	// a nearest neighbor in the set to point p; null if the set is empty 
	public Point2D nearest(Point2D p) 
	{
		if(isEmpty()) return null;

		Point2D nearest = null;

		for(Point2D p2 : set)
		{
			if(nearest == null || p2.distanceTo(p) < p.distanceTo(nearest))
			{
				nearest = p2;
			}
		}

		return nearest;
	}

	// unit testing of the methods (optional) 
	public static void main(String[] args) 
	{
	}

}
