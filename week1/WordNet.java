// https://class.coursera.org/algs4partII-007/assignment/view?assignment_id=5
//

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.KosarajuSharirSCC;

public class WordNet {

    private class Synset {
        private int index;
        private List<String> words = new ArrayList<String>();
        //String Description;
    }

    private HashMap<String, ArrayList<Synset>> words = new HashMap<String, ArrayList<Synset>>();
    private HashMap<Integer, Synset> synsetIndexes = new HashMap<Integer, Synset>();
    private Digraph digraph = null;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms)
    {
        if (synsets == null)
            throw new java.lang.NullPointerException();
        if (hypernyms == null)
            throw new java.lang.NullPointerException();

        debug(" (DEBUG) WordNet('" + synsets + "', '" + hypernyms + "')");
        constructSynsets(synsets);
        constructHypernyms(hypernyms);

        debug(" (DEBUG) WordNet constructed");
    }

    private static void debug(String msg) {
        System.out.println(msg);
    }

    private void constructSynsets(String synsets) {
        In synsetsStream = new In(synsets);
        String line = null;
        int readSynsetsCount = 0;
        int readWordsCount = 0;
        while ((line = synsetsStream.readLine()) != null) {
            String[] fields = line.split(",");
            Synset synset = new Synset();
            synset.index = Integer.parseInt(fields[0]);
            synset.words.addAll(Arrays.asList(fields[1].split(" ")));
            //synset.Description = fields[2];
            synsetIndexes.put(synset.index, synset);
            for (String word : synset.words) {
                ArrayList<Synset> relatedSynsets = words.get(word);
                relatedSynsets = relatedSynsets != null ? relatedSynsets : new ArrayList<Synset>();
                relatedSynsets.add(synset);
                words.put(word, relatedSynsets);
                readWordsCount++;
            }
            readSynsetsCount++;
        }

        assert !synsetsStream.isEmpty() && synsetIndexes.size() != 0;
        assert synsetIndexes.size() == readSynsetsCount;
        assert words.size() == readWordsCount;

        debug(" (DEBUG) synsetIndexes.size() == " + synsetIndexes.size());
        debug(" (DEBUG) words.size() == " + words.size());
    }

    private void constructHypernyms(String hypernyms) { 
        // read hypernyms file
        In hypernymsStream = new In(hypernyms);
        digraph = new Digraph(synsetIndexes.size());
        int numberOfNotRoots = 0;
        String line;
        while ((line = hypernymsStream.readLine()) != null) {
            // 1. parse:
            String[] fields = line.split(",");
            ArrayList<Integer> parsed = new ArrayList<Integer>(fields.length);
            for (String field : fields) {
                parsed.add(Integer.parseInt(field));
            }

            // 2. load into digraph
            int v = parsed.get(0);
            numberOfNotRoots++;
            for (int i = 1; i < parsed.size(); i++) {
                int w = parsed.get(i);
                digraph.addEdge(v, w);
            }
        }

        int numberOfRoots = digraph.V() - numberOfNotRoots;
        if (numberOfRoots > 1) 
            throw new java.lang.IllegalArgumentException("Unexpected number of roots: " + numberOfRoots);

        // Check that there are no cycles:
        KosarajuSharirSCC scc = new KosarajuSharirSCC(digraph);
        if (scc.count() != digraph.V())
            throw new java.lang.IllegalArgumentException("Hypernyms should not contain cycles.");

        debug(" (DEBUG) digraph.V() == " + digraph.V());
        debug(" (DEBUG) digraph.E() == " + digraph.E());
    }

    // returns all WordNet nouns
    public Iterable<String> nouns()
    { 
        return words.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word)
    { 
        if (word == null)
            throw new java.lang.NullPointerException();
        return words.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB)
    { 
        if (nounA == null || !isNoun(nounA))
            throw new java.lang.NullPointerException();
        if (nounB == null || !isNoun(nounB))
            throw new java.lang.NullPointerException();
        if (!isNoun(nounA))
            throw new java.lang.IllegalArgumentException();
        if (!isNoun(nounB))
            throw new java.lang.IllegalArgumentException();

        SAP sap = new SAP(digraph);

        ArrayList<Integer> v = getIdsByWord(nounA);
        ArrayList<Integer> w = getIdsByWord(nounB);
        return sap.length(v, w);
    }

    private ArrayList<Integer> getIdsByWord(String word) {
        ArrayList<Synset> synsets = words.get(word);
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (Synset s : synsets) 
            ids.add(s.index);
        return ids;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB)
    { 
        if (nounA == null)
            throw new java.lang.NullPointerException();
        if (nounB == null)
            throw new java.lang.NullPointerException();
        if (!isNoun(nounA))
            throw new java.lang.IllegalArgumentException();
        if (!isNoun(nounB))
            throw new java.lang.IllegalArgumentException();

        debug(" (DEBUG) sap('" + nounA + "', '" + nounB + "')");
        SAP sap = new SAP(digraph);

        ArrayList<Integer> v = getIdsByWord(nounA);
        ArrayList<Integer> w = getIdsByWord(nounB);
        int a = sap.ancestor(v, w);
        debug(" (DEBUG) sap(), a == " + a);
        if (a != -1)
            return synsetIndexes.get(a).words.get(0);
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        shouldThrowIfNotRootedDAG();
        shouldThrowIfCycles();
        shouldFindAncestorForWormAndBird();
    }

    private static void shouldThrowIfNotRootedDAG() {
        boolean isSuccess = false;
        try {
            new WordNet("wordnet\\synsets3.txt", "wordnet\\hypernyms3InvalidTwoRoots.txt");
        }
        catch (java.lang.IllegalArgumentException e) {
            isSuccess = true;
        }
        if (isSuccess)
            debug("should_throw_if_two_roots SUCCESS");
        else
            debug("should_throw_if_two_roots FAIL");

        isSuccess = false;
        try {
            new WordNet("wordnet\\synsets6.txt", "wordnet\\hypernyms6InvalidTwoRoots.txt");
        }
        catch (java.lang.IllegalArgumentException e) {
            isSuccess = true;
        }
        if (isSuccess)
            debug("should_throw_if_two_roots for 6 SUCCESS");
        else
            debug("should_throw_if_two_roots for 6 FAIL");
    }

    private static void shouldThrowIfCycles() {
        boolean isSuccess = false;
        try {
            new WordNet("wordnet\\synsets3.txt", "wordnet\\hypernyms3InvalidCycle.txt");
        }
        catch (java.lang.IllegalArgumentException e) {
            isSuccess = true;
        }
        if (isSuccess)
            debug("should_throw_if_cycles SUCCESS");
        else
            debug("should_throw_if_cycles FAIL");

        isSuccess = false;
        try {
            new WordNet("wordnet\\synsets6.txt", "wordnet\\hypernyms6InvalidCycle.txt");
        }
        catch (java.lang.IllegalArgumentException e) {
            isSuccess = true;
        }
        if (isSuccess)
            debug("not_rooted_DAG_cicle for 6 SUCCESS");
        else
            debug("not_rooted_DAG_cicle for 6 FAIL");
    }

    private static void shouldFindAncestorForWormAndBird() {
        WordNet wn = new WordNet("wordnet\\synsets.txt", "wordnet\\hypernyms.txt");
        String ancestor = wn.sap("worm", "bird");
        String expected = "animal";
        if (ancestor.equals(expected))
            debug("shouldFindAncestorForWormAndBird SUCCESS");
        else
            debug("shouldFindAncestorForWormAndBird FAIL: " + expected + " != " + ancestor);
    }
}
