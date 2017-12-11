import edu.princeton.cs.algs4.BinaryStdIn;

import java.util.ArrayList;

/*
NEXT: Pass memory test

Memory usage of a CircularSuffixArray for a random string of length n.
Maximum allowed memory is 64n + 128.

                 n        bytes
-------------------------------
=> FAILED       16        41224
=> FAILED       32        80088
=> FAILED       64       177256
=> FAILED      128       362952
=> FAILED      256       762424
=> FAILED      512      1451208
=> FAILED     1024      2694856
=> FAILED     2048      5218872
=> FAILED     4096     10580104
=> FAILED     8192     22281048
==> 0/10 tests passed

Total: 0/10 tests passed!

Estimated student memory (bytes) = 0.03 n^2 + 2452.53 n + 69906.82   (R^2 = 1.000)

 */


public class CircularSuffixArraySuffixTree {
    private class SuffixTree {
        private static final int R = 256;        // extended ASCII

        private class Node {
            Node[] next = new Node[R];
            int lo = -1; // start of suffix
            int length = -1; // length
            ArrayList<Integer> strings = new ArrayList<Integer>();
        }

        private Node root;
        private String s;
        private int length;
        private int[] suffixArray;
        private int sorted_inx;  // used by walkInorder. NEXT : wrap it

        private SuffixTree(String s) {
            this.s = s;
            this.length = s.length();
            this.root = new Node();
            this.suffixArray = new int[s.length()];
            for(int i = 0; i < this.s.length(); i++) {
                char c = this.s.charAt(i);
                this.root.next[c] = this.add(this.root.next[c], i, 0);
            }
        }

        public void print(){
            System.out.print("\nSuffix tree '" + this.s + "' \n");
            int[] array = getSuffixArray();
            System.out.print("\n array (" + (this.sorted_inx) + ")  ");
            for(int i = 0; i < array.length; i++) {
                System.out.print(i + ":" + array[i] + " ");
            }
        }

        public int[] getSuffixArray() {
            // now inorder iterate and get sorted suffix array
            this.sorted_inx = 0;
            walkInorder(this.root, 0);
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
                // 2.3 string part == suffix part:  (AB)CDE - (AB)DDD,  (B)A - (B)B
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
                    secondSuffixPart.next = node.next;
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

        private void walkInorder(Node v, int level) {
            //if(v.lo >= 0){
            //    System.out.print("\n");
            //    for(int i = 0; i < level; i++) System.out.print(".");
            //    for(int i = 0; i < v.length; i++)
            //        System.out.print(this.s.charAt((v.lo + i)%this.s.length()));
            //}

            for(int ord = 0; ord < R; ord++) {
                Node w = v.next[ord];
                if(w != null)
                    walkInorder(w, level + 1);
            }
            for(Integer inx : v.strings) {
                this.suffixArray[this.sorted_inx] = inx;
                this.sorted_inx++;
            }
        }
    }


    private final String s;
    private final int[] index;
    private SuffixTree st;

    // circular suffix array of s
    public CircularSuffixArraySuffixTree(String s) {
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
        if(args.length > 0 && args[0].charAt(0) == 't') {
            // T1
            /*
            AAABBABBAB 3
            AABBABBABA 4
            ABAAABBABB 1
            ABBABAAABB 8
            ABBABBABAA 5
            BAAABBABBA 2
            BABAAABBAB 0
            BABBABAAAB 7
            BBABAAABBA 9
            BBABBABAAA 6
             */
            String s = "BABAAABBAB";
            CircularSuffixArraySuffixTree csa = new CircularSuffixArraySuffixTree(s);
            int[] expected = {3, 4, 1, 8, 5, 2, 0, 7, 9, 6};
            for(int i = 0; i < s.length(); i++) {
                if(csa.index(i) != expected[i]) {
                    System.out.print("FAILED:");
                    System.out.print("i=" + i + " expected: " + expected[i] + ", actual " +csa.index(i));
                    return;
                }
            }
        }
        else {
            String input = BinaryStdIn.readString();
            CircularSuffixArraySuffixTree csa = new CircularSuffixArraySuffixTree(input);
            System.out.println(input);
            System.out.println();
            for(int i = 0; i < input.length(); i++)
                System.out.print(i+":"+csa.index(i) + " ");
            System.out.println();
        }
    }
}

