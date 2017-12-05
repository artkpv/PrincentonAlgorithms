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
   walkInorder(this.root, 0);
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
   }
   // 2. leaf or suffix
   else {
	   // create a common suffix 
	   int lo1 = node.lo, hi1 = node.hi;
	   if(node.strings.size() > 0) {
			lo1 = node.strings[0] + depth_inx;
			hi1 = (lo1 + length - depth_inx - 1)%length;
	   }
	   int lo2 = string_inx + depth_inx;
	   int hi2 = (lo2 + length - depth_inx - 1)%length;
	   int prefix_length = get_longest_prefix_length(lo1, hi1, lo2, hi2);
	   
	   // 1. whole string match:
	   if (depth_inx + prefix_length == length)  {
		   node.strings.add(string_inx);
		   return node;
	   }
	   // 2. part match:
	   else {
		   Node newSuffix = new Node();
		   if(prefix_length > 1) { // can be one char node or +1 char
			   newSuffix.lo = lo1;
			   newSuffix.hi = lo1 + prefix_length - 1;
		   }
		   
		   Node oldNode = new Node();
		   oldNode.strings = node.strings;
		   newSuffix.next[lo1 + prefix_length] = oldNode;

		   Node newNode = new Node();
		   newNode.strings.add(string_inx);
		   newSuffix.next[lo2 + prefix_length] = newNode;

		   return newSuffix;	   
	   }

   }
   // 3. suffix
   else {
	   assert node.hi != -1;
	   assert node.lo != -1;
	   int suffix_length = node.hi - node.lo + 1;
	   int matched = 0; 
	   // TODO find number of matching chars in this suffix
	   if(matched == suffix_length) {
		   // 1. suffix matched this string
		   // TODO add into subtree
		   return node;
	   }
	   else {
		   // 2. only part of suffix matched
		   // TODO divide this suffix into to suffixes or one char node if only first matched
		   Node newSuffix;

		   return newSuffix;
	   }
   }


   // Get common suffix. Given two suffixes: 1 - d .. length; 2 - node.lo .. node.hi
   int i = d; 
   int first = -1,  
    second = -1;
   // find last matching chars in two suffixes:
   while(i < length) {
    first = (string_inx + i)%length;
    second = (node.lo + i)%length;
    boolean out_of_suffix = second > node.hi;
    boolean chars_not_match = this.s.charAt(first) != this.s.charAt(second);
    if(out_of_suffix || chars_not_match) {  
     first--; 
     second--;
     break;
    }
    // else suffixes match so continue:
    i++;
   } 
   
   // 1. found the same suffix:
   if(first == (string_inx + length-1)%length) {
    node.strings.add(string_inx);
    return node;
   }
   // 2. go further down as `node` matches all:
   else if(second == node.hi) {  
    int nextCharInx = (first + 1)%length;
    node.next[nextCharInx] = this.add(node.next[nextCharInx],
      string_inx,
      1 + first - string_inx);
    return node;
   }
   // 3. divide old node (can be suffix or part of it)
   else { 
    // create new parent for this suffix and older ones:
    Node newSuffix = new Node();
    newSuffix.lo = node.lo;
    newSuffix.hi = second;

    int newSuffixNextInx = (first+1)%l;
    int oldNewSuffixCharInx = (second+1)%l;
	assert this.s.charAt(newSuffixNextInx) != this.s.charAt(oldNewSuffixCharInx);
    
    // old suffixes:
    Node dividedNode = new Node();
    dividedNode.next = node.next;
    dividedNode.lo = oldNewSuffixCharInx;
    dividedNode.hi = node.hi;
    dividedNode.strings = node.strings;  // if it was suffix (not part of it)
    newSuffix.next[this.s.charAt(oldNewSuffixCharInx)] = dividedNode;

    // new suffix:
    newSuffix.next[this.s.charAt(newSuffixNextInx)] = 
     this.add(newSuffix.next[this.s.charAt(newSuffixNextInx)],
       string_inx,
       1 + first - string_inx);

    return newSuffix;
   }
  }

  private void walkInorder(Node v, int level) {
   for(int i = 0; i < level; i++)
    System.out.print(" ");
   char ch = '-';
   if(v.lo > -1)
    ch = this.s.charAt(v.lo);
   System.out.println(ch + " " + v.lo + " " + v.hi + ", " + v.strings.size() + " suffixes ");

   for(Node w : v.next) {
    if(w != null)
     walkInorder(w, level + 1);
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
