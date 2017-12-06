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
        private int order;  // used by walkInorder. TODO : wrap it

        public SuffixTree(String s) {
            this.s = s;
            this.length = s.length();
            this.root = new Node();
            this.suffixArray = new int[s.length()];
            for(int i = 0; i < this.s.length(); i++) {
                char c = this.s.charAt(i);
                this.root.next[c] = this.add(this.root.next[c], i, 0);
                System.out.print(this.toString());
            }
        }

        public String toString(){
            this.order = 0;
            StringBuilder walk = this.walk_inorder(this.root, 0);
            StringBuilder sb = new StringBuilder();
            sb.append("Suffix tree: " + (this.order-1) + " nodes:");
            sb.append(walk.toString());
            sb.append("\n array ");
            for(int i = 0; i < this.suffixArray.length; i++) {
                sb.append(i + ":" + this.suffixArray[i] + " ");
            }
            return sb.toString();
        }

        public int[] getSuffixArray() {
            // now inorder iterate and get sorted suffix array
            this.order = 0;
            walk_inorder(this.root, 0);
            return this.suffixArray;
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
                while(common_suffix_length <= Math.min(string_remaining_length, node.length)) {
                    char string_char = this.s.charAt((string_inx + depth_inx + common_suffix_length - 1)%length);
                    char suffix_char = this.s.charAt((node.lo + common_suffix_length - 1)%length);
                    if(string_char != suffix_char)
                        break;
                    common_suffix_length++;
                }
                common_suffix_length--;
                assert common_suffix_length <= node.length;
                assert common_suffix_length <= string_remaining_length;

                // 2.1 all string == all suffix:  (A) - (A); (ABCD) - (ABCD)
                if(common_suffix_length == string_remaining_length) {
                    assert node.strings.size() > 0;
                    node.strings.add(string_inx);
                    return node;
                }
                // 2.2 string part == all suffix: (AB)CDE - (AB), (A)BCDE - (A)
                else if(common_suffix_length == node.length) {
                    assert common_suffix_length < string_remaining_length;

                    int next_depth_inx = depth_inx + common_suffix_length;
                    char next_char = this.s.charAt((string_inx + next_depth_inx)%length);
                    node.next[next_char] = this.add(node.next[next_char], string_inx, next_depth_inx);
                    return node;
                }
                // 2.3 string part == suffix part:  (AB)CDE - (AB)DDD
                else {
                    assert node.length > common_suffix_length;
                    assert string_remaining_length > common_suffix_length;
                    Node firstSuffixPart = new Node();
                    firstSuffixPart.lo = node.lo;
                    firstSuffixPart.length = common_suffix_length;

                    Node secondSuffixPart = new Node();
                    secondSuffixPart.lo = (node.lo + common_suffix_length) % length;
                    secondSuffixPart.length = node.length - common_suffix_length;
                    secondSuffixPart.strings = node.strings;
                    char secondSuffixPartChar = this.s.charAt(secondSuffixPart.lo);
                    firstSuffixPart.next[secondSuffixPartChar] = secondSuffixPart;

                    int next_depth_inx = depth_inx + common_suffix_length;  // TODO: duplicate
                    char next_char = this.s.charAt((string_inx + next_depth_inx)%length);
                    firstSuffixPart.next[next_char] =
                            this.add(firstSuffixPart.next[next_char], string_inx, next_depth_inx);

                    return firstSuffixPart;
                }
            }
        }

        private StringBuilder walk_inorder(Node v, int level) {
            StringBuilder sb = new StringBuilder();
            // print:
            sb.append("\n");
            for(int i = 0; i < level; i++)  sb.append(" ");
            sb.append(".");
            for(int i = 0; i < v.length; i++ ) sb.append(this.s.charAt((v.lo+i)%this.s.length()));
            for(Integer i : v.strings) sb.append(" " + i);

            for(int ord = 0; ord < this.R; ord++) {
                Node w = v.next[ord];
                if(w != null)
                    sb.append(walk_inorder(w, level + 1));
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
    public String toString() { return this.s + "\n" + this.st.toString() + "\n"; }

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

