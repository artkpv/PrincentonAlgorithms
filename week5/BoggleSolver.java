// Algorithms, part II. Week 5.
// See:
// - http://coursera.cs.princeton.edu/algs4/assignments/boggle.html
// - http://coursera.cs.princeton.edu/algs4/checklists/boggle.html
//

import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.Math;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.TrieST;

//import java.util.HashMap;
//import java.util.Arrays;

public class BoggleSolver
{
    private TrieST dictionaryTrie;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dictionaryTrie = new TrieST();

        for(String w : dictionary) 
            dictionaryTrie.put(w, w);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        ArrayList<String> words = new ArrayList<String>();
        for(int i = 0; i < board.rows(); i++){
            for(int j = 0; j < board.cols(); j++){
                words.addAll(getAllValidWords(board, i, j));

            }
        }
        return words;
    }

    // depth first search on board
    private class BoardIterator {

        private int[] edgeTo; // previous edge
        private BoggleBoard board;
        private int current;

        public BoardIterator(BoggleBoard myBoard, int row, int col) {
            board = myBoard;
            current = getNumber(row, col);
            edgeTo = new int[board.rows() * board.cols()];
            for(int i = 0; i < edgeTo.size(); i++) {
                edgeTo[i] = -1;
            }
            edgeTo[current] = -1;
        }

        public String next() {
            int next = nextInternal(current);

            if(next == -1) // all paths
                return null;

            // iterate:
            edgeTo[next] = current;
            current = next;

            // build string:
            StringBuilder b = new StringBuilder();
            int v = current;
            do {
                int[] rowCol = getRowCol(v);
                b.insert(0, board.getLetter(rowCol[0], rowCol[1]));
            } while((v = edgeTo[v]) != -1) { 

            return b.toString();
        }

        private int nextInternal(int v) {
            return nextInternal(v, -1);
        }

        private int nextInternal(int v, int previous) {
            if(v == -1)
                return -1;

            int next;

            // search next clockwise and not in edges till end of clockwise:
            while((next = getClockwise(v, next)) != -1 && edgeTo[next] != -1 && next != previous) 
                ;

            if(next != -1) {
                return next;
            }
            // run out of clockwise for current, get back:
            else {
                int w = edgeTo[v];
                edgeTo[v] = -1;
                return nextInternal(w, v);
            }
        }

        private int getClockwise(int v, int previous) {
            int[] rowCol = getRowCol(v);
            int row = rowCol[0];
            int col = rowCol[1];

            // TODO 


            int[] rowColPrevious = getRowCol(previous);




        }

        // stops searching current path and switch to the next 
        public void nextPath() {
        }

        private int getNumber(int row, int col) {
            return board.cols() * row + col;
        }

        private int[] getRowCol(int number) {
            return new int[] { Math.floor(number / board.cols() ), number % board.cols() } 
        }
    }

    private Iterable<String> getAllValidWords(BoggleBoard board, int row, int col) {
        ArrayList<String> found = new ArrayList<String>();

        String current;
        BoardIterator iterator = new BoardIterator(board, row, col);
        while((current = iterator.next() != null) {
            found.add(words.get(current));
            if(!words.keysWithPrefix(current).iterator().hasNext()){
                iterator.nextPath();
            }
        }

        return words;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String w) {
        if(dictionaryTrie.contains(w)) {
            if(w.length < 3) return 0;
            if(w.length < 5) return 1;
            if(w.length == 5) return 2;
            if(w.length == 6) return 3;
            if(w.length == 7) return 5;
            if(w.length > 7) return 11;
        }
        return 0;
    }

    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
