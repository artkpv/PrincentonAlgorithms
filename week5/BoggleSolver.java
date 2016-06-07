// Algorithms, part II. Week 5.
// See:
// - http://coursera.cs.princeton.edu/algs4/assignments/boggle.html
// - http://coursera.cs.princeton.edu/algs4/checklists/boggle.html
//

import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;
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
                Stack<Point> path = new Stack<Point>();
                Point p = new Point(i, j);
                path.push(p);
                StringBuilder wordBuilder = new StringBuilder();
                append(wordBuilder, board, p);
                walk(board, path, words, wordBuilder);
            }
        }
        trace("found " + words.size() + " words");
        return words;
    }

    private void append(StringBuilder sb, BoggleBoard board, Point p) {
        char l = board.getLetter(p.Row, p.Col);
        sb.append(l);
        if(l == 'Q')
            sb.append('U');
    }

    private void remove(StringBuilder sb, BoggleBoard board, Point p) {
        char l = board.getLetter(p.Row, p.Col);
        sb.deleteCharAt(sb.length() - 1); 
        if(l == 'Q')
            sb.deleteCharAt(sb.length() - 1); // 'U' was removed
    }


    private void walk(BoggleBoard board, Stack<Point> path, ArrayList<String> found, StringBuilder wordBuilder) {
        Point v = path.peek();
        for(Point p : getAdj(board, v)) {
            if(path.contains(p))
                continue;

            path.push(p);
            append(wordBuilder, board, p);
            String current = wordBuilder.toString();

            if(current.length() < 3) {
                walk(board, path, found, wordBuilder);
            }
            else {
                Iterable<String> keys = dictionaryTrie.keysWithPrefix(current);
                int left = 0;
                for(String k : keys) {
                    if(k == current)
                        if(!found.contains(current))
                            found.add(current);
                    else
                        left++;
                }
                if(left > 0)
                    walk(board, path, found, wordBuilder);
            }

            path.pop();
            remove(wordBuilder, board, p);
        }
    }

    private Iterable<Point> getAdj(BoggleBoard board, Point p) {
        ArrayList<Point> adj = new ArrayList<Point>();
        for(int i = p.Row - 1; i <= p.Row + 1; i++) {
            for(int j = p.Col - 1; j <= p.Col + 1; j++) {
                boolean atBoard = (0 <= i && i < board.rows()) && (0 <= j && j < board.cols());
                boolean atCenter = i == p.Row && j == p.Col;
                if(!atBoard || atCenter)
                    continue;
                adj.add(new Point(i, j));
            }
        }
        return adj;
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

    private class Point {
        public int Row, Col;

        public Point(int row, int col) {
            Row = row;
            Col = col;
        }

        public boolean equals(Object other) {
            if (other == this) return true;
            if (other == null) return false;
            if (getClass() != other.getClass()) return false;
            Point point = (Point)other;
            return (Row == point.Row && Col == point.Col);
        }

        public int hashCode() {
            int hash = Row;
            return hash * 17  + Col;
        }
    }

    private void trace(String msg) {
        //StdOut.println(msg);
    }
}
