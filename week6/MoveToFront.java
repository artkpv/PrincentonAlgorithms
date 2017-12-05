import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Alphabet;

public class MoveToFront {
	private char[] alphabet;     // the characters in the alphabet
	private int[] inverse;       // indices
	private final int R;

	// apply move-to-front encoding, reading from standard input and writing to standard output
	public static void encode() {
		MoveToFront mtf = new MoveToFront();
		while (!BinaryStdIn.isEmpty()) {
			char c = BinaryStdIn.readChar();
			//BinaryStdOut.write(c);
			BinaryStdOut.write(mtf.move_to_front(c));
		}
		BinaryStdOut.close();
	}


	// apply move-to-front decoding, reading from standard input and writing to standard output
	public static void decode() {
	}

	// if args[0] is '-', apply move-to-front encoding
	// if args[0] is '+', apply move-to-front decoding
	public static void main(String[] args) {
		if (args.length < 1) {
			StdOut.println("usage: MoveToFront +/-");
			return;
		}

		if (args[0].charAt(0) == '-')
			encode();
		if (args[0].charAt(0) == '+')
			decode();
	}

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
		for(int i = inx-1; i >= 0; i--) {
			char t = alphabet[i];
			inverse[(int)t]++;
			alphabet[i+1] = alphabet[i];
		}
		inverse[(int)c] = 0;
		alphabet[0] = c;
		return (byte)inx;
	}

}
