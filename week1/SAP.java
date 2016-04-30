import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v >= digraph.V())
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (digraph.V()-1));
        if (w < 0 || w >= digraph.V())
            throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (digraph.V()-1));

        BreadthFirstDirectedPaths bfForV = new BreadthFirstDirectedPaths(digraph, v);

        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[digraph.V()];
        int[] distTo = new int[digraph.V()];
        q.enqueue(w);
        distTo[w] = 0;
        int minLength = -1;
        while (!q.isEmpty()) {
            int r = q.dequeue();

            if (bfForV.hasPathTo(r)) {
                int length =  bfForV.distTo(r) + distTo[r];
                if (length < minLength)
                    minLength = length; 
            }

            for (int s : digraph.adj(r)) {
                if (!marked[s]) {
                    marked[s] = true;
                    distTo[s] = distTo[r] + 1;
                    q.enqueue(s);
                }
            }
        }
        return minLength;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v >= digraph.V())
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (digraph.V()-1));
        if (w < 0 || w >= digraph.V())
            throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (digraph.V()-1));
        BreadthFirstDirectedPaths bfForV = new BreadthFirstDirectedPaths(digraph, v);

        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[digraph.V()];
        int[] distTo = new int[digraph.V()];
        q.enqueue(w);
        int minLength = -1;
        int ancestor = -1;
        while (!q.isEmpty()) {
            int r = q.dequeue();
            if (bfForV.hasPathTo(r)) {
                int length =  bfForV.distTo(r) + distTo[r];
                if (length < minLength) {
                    minLength = length; 
                    ancestor = r;
                }
            }

            for (int s : digraph.adj(r)) {
                if (!marked[s]) {
                    marked[s] = true;
                    distTo[s] = distTo[r] + 1;
                    q.enqueue(s);
                }
            }
        }
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new java.lang.NullPointerException();
        BreadthFirstDirectedPaths bfForV = new BreadthFirstDirectedPaths(digraph, v);

        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[digraph.V()];
        int[] distTo = new int[digraph.V()];

        for (int r : w) {
            q.enqueue(r);
            distTo[r] = 0;
        }
        while (!q.isEmpty()) {
            int r = q.dequeue();
            if (bfForV.hasPathTo(r))
                return bfForV.distTo(r) + distTo[r];

            for (int s : digraph.adj(r)) {
                if (!marked[s]) {
                    marked[s] = true;
                    distTo[s] = distTo[r] + 1;
                    q.enqueue(s);
                }
            }
        }
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new java.lang.NullPointerException();
        BreadthFirstDirectedPaths bfForV = new BreadthFirstDirectedPaths(digraph, v);

        Queue<Integer> q = new Queue<Integer>();
        boolean[] marked = new boolean[digraph.V()];

        for (int r : w) {
            q.enqueue(r);
        }
        while (!q.isEmpty()) {
            int r = q.dequeue();
            if (bfForV.hasPathTo(r))
                return r;

            for (int s : digraph.adj(r)) {
                if (!marked[s]) {
                    marked[s] = true;
                    q.enqueue(s);
                }
            }
        }
        return -1;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        StdOut.printf(G.toString());
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
