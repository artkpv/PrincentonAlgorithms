import edu.princeton.cs.algs4.*;

import java.util.ArrayList;

public class BurrowsWheeler {
    private static final int R = 256;        // extended ASCII
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform(){
        /*
         must be proportional to n + R (or better) in the worst case, excluding the time to construct the circular suffix array
         */
        String input = BinaryStdIn.readString();
        BinaryStdIn.close();
        CircularSuffixArray csa = new CircularSuffixArray(input);
        BinaryStdOut.write(csa.getFirstStringSortedInx());
        int l = input.length();
        for(int i = 0; i < l; i++) {
            int index_i = csa.index(i);
            char c = input.charAt((index_i + l - 1)%l);
            BinaryStdOut.write(c);
        }
        BinaryStdOut.close();
    }

    // Apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output.
    // Must be proportional to n + R (or better) in the worst case.
    public static void inverseTransform(){
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
        BinaryStdIn.close();

        // 1. generate `next` array used to invert to the original
        //    next[] - array where next[i] == i+1, where i index of original suffix

        // 1.1 get first col as indexes of last col:
        //     sort using Counting Sort ;   O(n) space / time
        ArrayList<ArrayList<Integer>> firstColCharGroups = new ArrayList<ArrayList<Integer>>(R);
        for(int c = 0; c < R; c++) firstColCharGroups.add(new ArrayList<Integer>());
        for(int i = 0; i < t.length(); i++)
            firstColCharGroups.get(t.charAt(i)).add(i);
        //   get number of chars before a group of chars (base):
        int[] firstColGroupBases = new int[R];
        firstColGroupBases[0] = 0;
        StringBuilder firstCol = new StringBuilder(t.length());
        firstCol.setLength(t.length());
        for(int i = 1; i < R; i++)
        {
            int base = firstColCharGroups.get(i - 1).size() + firstColGroupBases[i - 1];
            firstColGroupBases[i] = base;
            ArrayList<Integer> group = firstColCharGroups.get(i);
            for(int j = 0; j < group.size(); j++)
                firstCol.setCharAt(base + j, t.charAt(group.get(j)));
        }
        assert firstCol.charAt((int)firstCol.length()/2) != '\u0000';

        // 1.2 from sorted chars depict `next`
        int[] next = new int[t.length()];
        for(int c = 0; c < R; c++)
        {
            int base = firstColGroupBases[c];
            ArrayList<Integer> indexes = firstColCharGroups.get(c);
            for(int i = 0; i < indexes.size(); i++) {
                int firstColCharInx = base + i;
                int lastColCharInx = indexes.get(i);
                next[firstColCharInx] = lastColCharInx;
            }
        }

        // 2. invert, generate original string
        //    given sorted first column
        //    first char in output is at `first`
        //    next char in output is at next[first]
        //     .. so on
        StringBuilder original = new StringBuilder();
        original.setLength(t.length());
        int next_i = first;
        for(int i = 0; i < t.length(); i++)
        {
            char ch = firstCol.charAt(next_i);
            original.setCharAt(i, ch);
            next_i = next[next_i];
        }

        BinaryStdOut.write(original.toString());
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args){
        if (args.length < 1) {
            StdOut.println("usage: BurrowsWheeler +/-");
            return;
        }

        if (args[0].charAt(0) == '-')
            transform();
        if (args[0].charAt(0) == '+')
            inverseTransform();
    }
}
