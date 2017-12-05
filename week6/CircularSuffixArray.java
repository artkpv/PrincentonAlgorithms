import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Arrays;


public class CircularSuffixArray {
	class SuffixTree {
		private static final int R = 256;        // extended ASCII

		class Node {
			Node[] next = new Node[R];
			int lo = -1; // start of suffix
			int hi = -1; // end of suffix
			ArrayList<Integer> strings = new ArrayList<Integer>();
		}

		private Node root;
		private String s;
		private int length;
		private int[] suffixArray;
		private int order;  // used by walkInorder

		public SuffixTree(String s) {
			this.s = s;
			this.length = s.length();
			this.root = new Node();
			for(int i = 0; i < this.s.length(); i++) {
				char c = this.s.charAt(i);
				this.root.next[c] = this.add(this.root.next[c], i, 0);
			}
			this.suffixArray = new int[s.length()];
		}

		public int[] getSuffixArray() {
			// now inorder iterate and get sorted suffix array
			this.order = 0;
			walkInorder(this.root, 0, '-');
			return this.suffixArray;
		}

		private int get_longest_prefix_length(int lo1, int hi1, int lo2, int hi2) {
			// lo1 , hi1 - indexes of first string
			int l = 0; // longest prefix length
			int i = lo1, j = lo2;
			while(l <= this.length 
					&& this.s.charAt(i) == this.s.charAt(j)
					&& i <= hi1
					&& j <= hi2) {
				i = (i + 1)%this.length;
				j = (j + 1)%this.length;
				l++;
			}
			return l;
		}

		private Node add(Node node, int string_inx, int depth_inx) { 
			int length = this.length;
			int char_inx = (string_inx + depth_inx)%length;
			char c = this.s.charAt(char_inx);

			if(node == null) {  // 1. the remaining of this suffix makes a leaf (full or part)
				Node newnode = new Node();
				newnode.strings.add(string_inx);
				return newnode;
			}

			// 1. one char node:
			if(node.lo == -1) {
				node.next[c] = this.add(node.next[c], string_inx, depth_inx + 1);
				return node;
			}
			// 2. leaf or suffix
			else {
				// find common suffix
				int lo1 = node.lo, hi1 = node.hi;
				if(node.strings.size() > 0) {
					lo1 = (node.strings.get(0) + depth_inx)%length;
					hi1 = (node.strings.get(0) + length - 1)%length;
				}
				int lo2 = (string_inx + depth_inx)%length;
				int hi2 = (string_inx + length - 1)%length;
				int prefix_length = get_longest_prefix_length(lo1, hi1, lo2, hi2);

				// TODO: fix here. It creates only one char suffixes now (see output).

				// 1. whole suffix/leaf match
				if (prefix_length == hi1 - lo1 + 1)  {
					if(node.strings.size() > 0) { // leaf
						node.strings.add(string_inx);
					}
					else {  // suffix
						int newDepthInx = (depth_inx + prefix_length);
						char nextC = this.s.charAt((string_inx + newDepthInx)%length);
						node.next[nextC] = this.add(node.next[nextC], string_inx, newDepthInx);
					}
					return node;
				}
				// 3. part of suffix/leaf match:
				else {
					Node newSuffix = new Node();
					if(prefix_length > 1) { // can be one char node or +1 char
						newSuffix.lo = lo1;
						newSuffix.hi = lo1 + prefix_length - 1;
					}

					// add second part of this divided suffix/leaf:
					Node secondPart = new Node();
					secondPart.strings = node.strings;
					int secondPartLo = (lo1 + prefix_length)%length;
					char secondPartChar = this.s.charAt(secondPartLo);
					if(node.lo != -1 && secondPartLo != node.hi) {
						secondPart.lo = secondPartLo;
						secondPart.hi = node.hi;
					}
					newSuffix.next[secondPartChar] = secondPart;

					Node newNode = new Node();
					newNode.strings.add(string_inx);
					int newNodeLo = (lo2 + prefix_length)%length;
					char newNodeChar = this.s.charAt(newNodeLo);
					assert newNodeChar != secondPartChar;
					newSuffix.next[newNodeChar] = newNode;

					return newSuffix;    
				}
			}
		}

		private void walkInorder(Node v, int level, char c) {
			// print:
			// level:
			for(int i = 0; i < level; i++)
				System.out.print(" ");
			// suffix:
			if(v.lo > -1) {
				for(int i = v.lo; i == v.hi; ){
					System.out.print(this.s.charAt(i));
					i = (i + 1)%this.s.length();
				}
			}
			else {
				System.out.print(c);
			}
			System.out.println(" " + v.lo + " " + v.hi + ", " + v.strings.size() + " suffixes ");

			for(int ord = 0; ord < this.R; ord++) {
				Node w = v.next[ord];
				if(w != null)
					walkInorder(w, level + 1, (char)ord);
			}
			for(Integer inx : v.strings) {
				this.suffixArray[inx] = this.order;
				this.order++;
			}
		}
	}


	private final String s;
	private final int[] index;

	// circular suffix array of s
	public CircularSuffixArray(String s) { 
		if(s == null)
			throw new IllegalArgumentException();
		this.s = s;
		SuffixTree st = new SuffixTree(s);
		this.index = st.getSuffixArray();
	}

	// length of s
	public int length() {
		return this.s.length();
	}

	// returns index of ith sorted suffix
	public int index(int i) {
		int l = this.length();
		if(i < 0 || l <= i)
			throw new IllegalArgumentException();
		return this.index[i];
	}

	// unit testing (required)
	public static void main(String[] args)  {
		// T1
		String s = "ABRACADABRA!";
		CircularSuffixArray csa = new CircularSuffixArray(s);
		System.out.print("'" + s + "' ");
		for(int i = 0; i < s.length(); i++)
			System.out.print(i + ":" + csa.index(i) + " ");
		System.out.println();

		assert csa.index(2) == 11: "csa.index(2) == 11";
		System.out.println("SUCCESS");
	}
}
