import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Arrays;


public class CircularSuffixArray {
    class SuffixTree {
        private static final int R = 256;        // extended ASCII

        class Node {
            Node[] next = new Node[R];
            int lo = -1; // start of suffix
            int length = -1; // length
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

        public String toString(){
            return this.walk_inorder(this.root, 0, ' ').toString();
        }

        public int[] getSuffixArray() {
            // now inorder iterate and get sorted suffix array
            this.order = 0;
            walk_inorder(this.root, 0, '-');
            return this.suffixArray;
        }

        private int get_longest_prefix_length(int lo1, int len1, int lo2, int len2) {
        }

        private Node add(Node node, int string_inx, int depth_inx) {
            int length = this.length;

            // 1. Free leaf
            if(node == null) {  // 1. the remaining of this suffix makes a leaf (full or part)
                Node newnode = new Node();
                newnode.lo = (string_inx + depth_inx)%length;
                newnode.length = length - depth_inx;
                newnode.strings.add(string_inx);
                return newnode;
            }
            // 2. Suffix
            else {
                // find common suffix length:
                int common_suffix_length = 1; // 1st char matches
                int string_remaining_length = length - depth_inx;
                while(common_suffix_length <= string_remaining_length && common_suffix_length > node.length ){
                    char string_char = this.s.charAt((string_inx + depth_inx + common_suffix_length)%length);
                    char suffix_char = this.s.charAt((node.lo + common_suffix_length)%length);
                    if(string_char != suffix_char)
                        break;
                    common_suffix_length++;
                }

                // 2.1 all string == all suffix:  (A) - (A); (ABCD) - (ABCD)
                if(common_suffix_length == string_remaining_length) {
                    assert node.strings.size() > 0;
                    node.strings.add(string_inx);
                    return node;
                }
                // 2.2 string part == all suffix: (AB)CDE - (AB)
                else if(common_suffix_length == node.length) {
                    assert common_suffix_length < string_remaining_length;
                    // TODO

                }
                // 2.3 string part == suffix part:  (AB)CDE - (AB)DDD
                else {
                    // TODO
                }
            }

            // 2. One char suffix
            else if(node.length == 1) {
                assert this.s.charAt(node.lo) == c;
                node.next[c] = this.add(node.next[c], string_inx, depth_inx + 1);
                return node;
            }
            // 3. longer suffix
            else {

                if(node.length == prefix_length){

                }
                else if(node.length) {
                }

                // 1. whole suffix/leaf match
                if (prefix_length == hi1 - lo1 + 1)  {  // TODO Bug: fix length of suffix
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

        private StringBuilder walk_inorder(Node v, int level, char c) {
            StringBuilder sb = new StringBuilder();
            // print:
            // level:
            for(int i = 0; i < level; i++)
                sb.append(" ");
            // suffix:
            if(v.lo > -1) {
                for(int i = v.lo; i == v.hi; ){
                    sb.append(this.s.charAt(i));
                    i = (i + 1)%this.s.length();
                }
            }
            else {
                sb.append(c);
            }
            sb.append(" " + v.lo + ".." + v.hi);
            for(Integer i : v.strings)
                sb.append(" " + i);

            for(int ord = 0; ord < this.R; ord++) {
                Node w = v.next[ord];
                if(w != null)
                    walk_inorder(w, level + 1, (char)ord);
            }
            for(Integer inx : v.strings) {
                this.suffixArray[inx] = this.order;
                this.order++;
            }
            return sb;
        }
    }


    private final String s;
    private final int[] index;
    private SuffixTree st;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if(s == null)
            throw new IllegalArgumentException();
        this.s = s;
        this.st = new SuffixTree(s);
        this.index = this.st.getSuffixArray();
    }

    @Override
    public String toString() {
        return this.s + "\n" + this.st.toString() + "\n";
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
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        System.out.print(csa.toString());

        assert csa.index(2) == 11: "csa.index(2) == 11";
        System.out.println("SUCCESS");
    }
}

