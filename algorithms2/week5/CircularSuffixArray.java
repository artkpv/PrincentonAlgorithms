import java.lang.IllegalArgumentException;
import java.util.ArrayList;

import edu.princeton.cs.algs4.*;


public class CircularSuffixArray {
    private final String s;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if(s == null)
            throw new IllegalArgumentException();
        this.s = s;
        this.index = new int[this.s.length()];
		for(int i = 0; i < this.s.length(); i++)
			this.index[i] = i;
        sort(0, this.s.length()-1);
    }

	private int compare(int lo1, int lo2) {
		int l = this.s.length();
		for(int i = 0; i < l; i++) {
			int cmp = this.s.charAt((lo1 + i)%l) - this.s.charAt((lo2 + i)%l);
			if(cmp != 0)
				return cmp;
		}
		return 0;
	}

	private void exch(int i, int j) {
		int t = this.index[i];
		this.index[i] = this.index[j];
		this.index[j] = t;
	}

	private void sort(int lo, int hi) {
		if (hi <= lo) return;
		int lt = lo, i = lo+1, gt = hi;
		int v = this.index[lo];
		while (i <= gt)
		{
			int cmp = compare(this.index[i], v);
			if (cmp < 0)		exch(lt++, i++);
			else if (cmp > 0)	exch(i, gt--);
			else i++;
		} // Now a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
		sort(lo, lt - 1);
		sort(gt + 1, hi);
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
			CircularSuffixArray csa = new CircularSuffixArray(s);
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
			CircularSuffixArray csa = new CircularSuffixArray(input);
			System.out.println(input);
			System.out.println();
			for(int i = 0; i < input.length(); i++)
				System.out.print(i+":"+csa.index(i) + " ");
			System.out.println();
		}
	}
}

