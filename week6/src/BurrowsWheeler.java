import edu.princeton.cs.algs4.*;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform(){
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
        int firstStringSortedInx = BinaryStdIn.readInt();
        String input = BinaryStdIn.readString();
        System.out.print(firstStringSortedInx);
        System.out.print(input);
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
