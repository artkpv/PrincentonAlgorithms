import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private char[] alphabet;     // the characters in the alphabet: inx -> char
    private int[] inverse;       // indices: char -> inx
    private final int R;

    public MoveToFront() {
        this.R = 256;
        alphabet = new char[R];
        inverse = new int[R];

        for (int i = 0; i < R; i++) {
            alphabet[i] = (char) i;
            inverse[i] = i;
        }
    }

    private byte move_to_front(char c) {
        int inx = inverse[(int)c];
        // move all chars before c one position right:
        for(int i = inx-1; i >= 0; i--) {
            char t = alphabet[i];
            inverse[(int)t]++;
            alphabet[i+1] = alphabet[i];
        }
        // put c at front:
        inverse[(int)c] = 0;
        alphabet[0] = c;
        return (byte)inx;
    }

    private byte move_to_front_decode(int charInx) {
        char c = alphabet[charInx];
        // move all chars before c one position right:
        for(int i = charInx-1; i >= 0; i--) {
            char t = alphabet[i];
            inverse[(int)t]++;
            alphabet[i+1] = alphabet[i];
        }
        // put c at front:
        inverse[charInx] = 0;
        alphabet[0] = c;
        return (byte)c;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        MoveToFront mtf = new MoveToFront();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(mtf.move_to_front(c));
        }
        BinaryStdOut.close();
    }


    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        MoveToFront mtf = new MoveToFront();
        while (!BinaryStdIn.isEmpty()) {
            int charInx = BinaryStdIn.readChar();
            BinaryStdOut.write(mtf.move_to_front_decode(charInx));
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length < 1) {
            BinaryStdOut.write("usage: MoveToFront +/-");
            return;
        }
        if (args[0].charAt(0) == '-')
            encode();
        if (args[0].charAt(0) == '+')
            decode();
    }
}

