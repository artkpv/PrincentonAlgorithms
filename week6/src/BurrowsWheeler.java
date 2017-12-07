import edu.princeton.cs.algs4.*;

public class BurrowsWheeler {
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

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform(){
        /*
        must be proportional to n + R (or better) in the worst case
3              - first
ARD!RCAAAABB   - sorted
!

1. next[] - redefined. Then used to reconstruct the original array.
         */
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();

        String firstCol = null; // TODO
        // Generates next array used to invert to the original
        // gets next[] - array where next[i] == i+1, where i index of original suffix

        // sort using Bucket Sort ;   O(n) space / time
        // but how to get source inx? make it int[int[]] ? int[char] - gets array of source indexes?

        int[] next = new int[]; // TODO
        // for each group of chars determine next[];  O(n) space / time

        String original = invertMessage(next, first, firstCol, t);
        BinaryStdOut.write(original);
    }

    // generate original string
    private static String invertMessage(int[] next, int first, String firstCol, String lastCol) {
        // given sorted first column
        // first char in output is at first
        // next char in output is at next[first]
        //  .. so on
        return null;
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
