import java.util.Comparator;

public class KdTree 
{
	private Node root;
	private static boolean HOR = true;
	private static boolean VER = false;
	private class Node
	{
		public Point2D point;
		public boolean split;
		public Node left;		
		public Node right;		
		public int count;

		public Node(Point2D p, int count, boolean split)
		{
			point = p;
			this.count = count;
			this.split = split;
		}
	}

	// construct an empty set of points 
	public KdTree() 
	{
	}

	// is the set empty? 
	public boolean isEmpty() 
	{
		return size() == 0;
	}

	// number of points in the set 
	public int size() 
	{
		return size(root);
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) 
	{
		root = insert(p, root, VER);
	}

	// does the set contain point p? 
	public boolean contains(Point2D p) 
	{
		return get(root, p) != null;
	}

	private Node get(Node x, Point2D p)
	{
		if(x == null) return null;

		Comparator<Point2D> comparator = x.split == VER ? Point2D.X_ORDER : Point2D.Y_ORDER;

		int cmp = comparator.compare(p, x.point);
		if(cmp < 0) 				return get(x.left, p);
		else if(cmp > 0) 			return get(x.right, p);
		else if(!x.point.equals(p)) return get(x.right, p);
		else						return x;
	}

	// draw all points to standard draw 
	public void draw() 
	{
		draw(root, new RectHV(0,0,1,1) );
	}

	private void draw(Node x, RectHV parentRect)
	{
		if(x == null) return;

		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(.01);
		x.point.draw();

		RectHV[] rects = getLeftAndRightRects(x, parentRect);
		RectHV rightRect = rects[1], leftRect = rects[0];

		StdDraw.setPenRadius();
		if(x.split == VER) 	
		{
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.line(leftRect.xmax(), leftRect.ymin(), leftRect.xmax(), leftRect.ymax());
		}
		else 				
		{
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.line(leftRect.xmin(), leftRect.ymax(), leftRect.xmax(), leftRect.ymax());
		}

		draw(x.left, leftRect);
		draw(x.right, rightRect);
	}

	private RectHV[] getLeftAndRightRects(Node x, RectHV parentRect)
	{
		RectHV rightRect, leftRect;
		if(x.split == VER) 	
		{
			leftRect = new RectHV(parentRect.xmin(), parentRect.ymin(), x.point.x(), parentRect.ymax());
			rightRect = new RectHV(x.point.x(), parentRect.ymin(), parentRect.xmax(), parentRect.ymax());
		}
		else 				
		{
			leftRect = new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), x.point.y());
			rightRect = new RectHV(parentRect.xmin(), x.point.y(), parentRect.xmax(), parentRect.ymax());
		}

		return new RectHV[] {leftRect, rightRect};
	}

	// all points that are inside the rectangle 
	public Iterable<Point2D> range(RectHV rect) 
	{
		Stack<Point2D> insiders = new Stack<Point2D>();
		range(insiders, root, rect, new RectHV(0,0,1,1));
		return insiders;
	}

	private void range(Stack<Point2D> insiders, Node x, RectHV rect, RectHV parentRect)
	{
		assert rect.intersects(parentRect);
		if(x == null) return; 
		if(rect.contains(x.point))	insiders.push(x.point);

		RectHV[] rects = getLeftAndRightRects(x, parentRect);
		RectHV rightRect = rects[1], leftRect = rects[0];

		if(leftRect.intersects(rect)) 	range(insiders, x.left, rect, leftRect);
		if(rightRect.intersects(rect)) 	range(insiders, x.right, rect, rightRect);

	}

	// a nearest neighbor in the set to point p; null if the set is empty 
	public Point2D nearest(Point2D p) 
	{
		return nearest(p, null, root, new RectHV(0,0,1,1));
	}

	private Point2D nearest(Point2D p, Point2D nearest, Node x, RectHV parentRect)
	{
		if(x == null) return nearest;

		if(nearest == null || x.point.distanceTo(p) < nearest.distanceTo(p)) nearest = x.point;

		if(parentRect.distanceTo(p) > nearest.distanceTo(p)) return nearest;

		RectHV[] rects = getLeftAndRightRects(x, parentRect);
		RectHV rightRect = rects[1], leftRect = rects[0];

		if(rightRect.contains(p))
		{
			nearest = nearest(p, nearest, x.right, rightRect);
			nearest = nearest(p, nearest, x.left, leftRect);
		}
		else
		{
			nearest = nearest(p, nearest, x.left, leftRect);
			nearest = nearest(p, nearest, x.right, rightRect);
		}

		return nearest;
	}



	private int size(Node x) 
	{
		if(x == null) return 0;
		return x.count;
	}

	private Node insert(Point2D p, Node x, boolean split)
	{
		if(x == null) return new Node(p, 1, split);

		assert x.split == split;

		Comparator<Point2D> comparator = x.split == VER ? Point2D.X_ORDER : Point2D.Y_ORDER;

		boolean nextSplit = split == VER ? HOR : VER;
		int cmp = comparator.compare(p, x.point);
		if(cmp < 0) 				x.left = insert(p, x.left, nextSplit);
		else if(cmp > 0) 			x.right = insert(p, x.right, nextSplit);
		else if(!x.point.equals(p)) x.right = insert(p, x.right, nextSplit);
		else 						x.point = p;

		x.count = 1 + size(x.left) + size(x.right);
		return x;
	}


	//
	//
	// unit testing of the methods (optional) 
	// =======================================
	//
	//
	public static void main(String[] args) 
	{
		ShouldGetSize();
		ShouldFindIfContains();
		ShouldFindRange();
	}

	private static void ShouldGetSize()
	{
		KdTree t = new KdTree();
		t.insert(new Point2D(1,1));
		assert t.size() == 1;
		t.insert(new Point2D(2,2));
		assert t.size() == 2;
		t.insert(new Point2D(3,3));
		assert t.size() == 3;
	}

	private static void ShouldFindIfContains()
	{
		KdTree t = new KdTree();
		t.insert(new Point2D(0.1, 0.1));
		t.insert(new Point2D(0.2, 0.2));
		t.insert(new Point2D(0.3, 0.3));
		t.insert(new Point2D(0.4, 0.3));
		t.insert(new Point2D(0.3, 0.4));
		t.insert(new Point2D(0.5, 0.3));

		assert t.contains(new Point2D(0.2,0.2));
		assert !t.contains(new Point2D(0.0, 0.0));
		assert t.contains(new Point2D(0.5, 0.3));
	}

	private static void ShouldFindRange()
	{
		KdTree t = new KdTree();
		t.insert(new Point2D(0.1, 0.1));
		t.insert(new Point2D(0.2, 0.2));
		t.insert(new Point2D(0.3, 0.3));
		t.insert(new Point2D(0.4, 0.3));
		t.insert(new Point2D(0.3, 0.4));
		t.insert(new Point2D(0.5, 0.5));

		SET<Point2D> expected = new SET<Point2D>();
		expected.add(new Point2D(0.5, 0.5));

		int size = 0;
		for(Point2D p : t.range(new RectHV(0.45, 0.45, 0.55, 0.55)))
		{
			assert expected.contains(p) : (" expected list does not contain " + p.toString());
			size++;
		}
		assert size == expected.size() : (" invalid size: " + size);
	}
}

