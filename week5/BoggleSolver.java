// Algorithms, part II. Week 5.
// See:
// - http://coursera.cs.princeton.edu/algs4/assignments/boggle.html
// - http://coursera.cs.princeton.edu/algs4/checklists/boggle.html
//

import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.TrieST;

//import java.util.HashMap;
//import java.util.Arrays;

public class BoggleSolver
{
    private TrieST<String> dictionaryTrie;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trace("constructing BoggleSolver");
        dictionaryTrie = new TrieST<String>();

        for(String w : dictionary) 
            dictionaryTrie.put(w, w);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        trace("gets words for board " + board.rows() + "x" + board.cols());
        ArrayList<String> words = new ArrayList<String>();
        for(int i = 0; i < board.rows(); i++){
            for(int j = 0; j < board.cols(); j++){
                words.addAll(getAllValidWords(board, i, j));
            }
        }
        trace("found " + words.size() + " words");
        return words;
    }

    private ArrayList<String> getAllValidWords(BoggleBoard board, int row, int col) {
        trace(" searching words at " + row + ", " + col);
        ArrayList<String> found = new ArrayList<String>();

        String current;
        BoardIterator iterator = new BoardIterator(board, row, col);
        while((current = iterator.next()) != null) {
            trace(" iterator.next() = '" + current + "'");
            if(current.length() < 3) 
                continue;

            Iterator<String> keys = dictionaryTrie.keysWithPrefix(current).iterator();

            String key;
            // nothing on that path:
            if(keys == null || !keys.hasNext() || (key = keys.next()) == null) 
            {
                // returned to start:
                if(current.length() == 3)
                    break;
                else
                    iterator.skipPath();
            }
            // only one left is current:
            else if(key == current) 
                found.add(key);
            // else many left

        }

        return found;
    }

    // TODO: make it depth first search fully (w/o previous node)
    // depth first search on board
    private class BoardIterator {

        private int[] edgeTo; // previous edge
        private BoggleBoard board;
        private int current, toSkip;

        public BoardIterator(BoggleBoard myBoard, int row, int col) {
            board = myBoard;

            edgeTo = new int[board.rows() * board.cols()];
            for(int i = 0; i < edgeTo.length; i++) {
                edgeTo[i] = -1;
            }

            current = getNumber(row, col);
            edgeTo[current] = current;
            toSkip = -1;
        }

        // bread first search:
        public String next() {
            trace("  BoardIterator|next() current=" + current + ", toSkip=" + toSkip);
            int next = nextInternal(current, toSkip);

            // run out for current:
            if(next == -1) {
                skipPath();
                // can not skip hence run out all:
                if(toSkip == -1) 
                    return null;
                // get next skipping current:
                return next();
            }

            toSkip = -1;
            edgeTo[next] = current;
            current = next;
            return getString(current);
        }

        public void skipPath() {
            trace("  BoardIterator|skipPath() current=" + current);
            int v = edgeTo[current];
            edgeTo[current] = -1;
            if(v == -1) // run out
                return;

            toSkip = current;
            current = v;
        }

        private int nextInternal(int v, int previous) {
            trace("  BoardIterator|nextInternal(" + v + ", previous=" + previous + ")");
            if(v == -1) return -1; // got to the beginning

            // convert indexes to rows, cols
            int[] rowCol = getRowCol(v);
            int row = rowCol[0], col = rowCol[1];
            int pRow = -1, pCol = -1;
            if(previous >= 0 ) {
                int[] rowColPrevious = getRowCol(previous);
                pRow = rowColPrevious[0]; pCol = rowColPrevious[1];
            }

            int rows = board.rows(), cols = board.cols();

            final int N = 3;
            for(int i = row - N/2; i <= row + N/2; i++) {
                for(int j = col - N/2; j <= col + N/2; j++) {
                    //trace("  BoardIterator|getNextAdj| i=" + i + ", j=" + j);
                    // out of board:
                    if(i < 0 || j < 0 || i >= rows || j >= cols) continue;
                    // center: 
                    if(i == row && j == col) continue;
                    // before previous:
                    if(i <= pRow && j <= pCol) continue;

                    int next = getNumber(i, j);
                    // attended before:
                    if(edgeTo[next] != -1)  continue;
                    else                    return next;
                }
            }
            return -1;
        }

        private String getString(int v) {
            trace(" BoggleIterator|getString(" + v + ")");
            trace(" BoggleIterator|getString|edgeTo:" );
            trace(get_string_of_edgeto());

            if(v == -1) return null;
            make_assert(0 <= v && v < edgeTo.length);

            StringBuilder b = new StringBuilder();
            while(v != -1){
                int[] rowCol = getRowCol(v);
                b.insert(0, board.getLetter(rowCol[0], rowCol[1]));
                int next = edgeTo[v];
                if(next == v)
                    break;
                v = next;
            }
            String str = b.toString();
            make_assert(str != null);
            return str;
        }

        private int getNumber(int row, int col) {
            make_assert(
                    (0 <= row && row < board.rows()) || 
                    (0 <= col && col < board.cols()));
            int number = board.cols() * row + col;
            make_assert(0 <= number && number < edgeTo.length);
            return number;
        }

        private int[] getRowCol(int number) {
            make_assert(0 <= number && number < edgeTo.length);
            int[] rowCol = 
                new int[] { (number / board.cols() ), number % board.cols() }; 
            make_assert(
                    (0 <= rowCol[0] && rowCol[0] < board.rows()) || 
                    (0 <= rowCol[1] && rowCol[1] < board.cols()));
            return rowCol;
        }

        private String get_string_of_edgeto() {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < edgeTo.length; i++) {
                if(i != 0 && i % board.cols() == 0)
                    sb.append("\n");
                int num = edgeTo[i];
                sb.append((num == -1 ? "?" : num) + " ");
            }
            return sb.toString();
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String w) {
        if(dictionaryTrie.contains(w)) {
            int l = w.length();
            if(l < 3) return 0;
            if(l < 5) return 1;
            if(l == 5) return 2;
            if(l == 6) return 3;
            if(l == 7) return 5;
            if(l > 7) return 11;
        }
        return 0;
    }

    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        StdOut.println(board.toString());
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private void trace(String msg) {
        StdOut.println(msg);
    }

    private static void make_assert(boolean check) {
        make_assert(check, null);
    }

    private static void make_assert(boolean check, String msg) {
        if(!check)
            throw new AssertionError("Assert failed. " + msg);
    }
}
