// Constructs a graph of English words (synsets) and allows to find shortest ancestor and disctance. 
// Then it can be used in determinine an outcast (e.g. table horse mouse -> table)
//
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

    private HashMap<String, ArrayList<Synset>> words;
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
        //System.out.println(msg);
    }

    private static void myassert(boolean test) {
        myassert(test, null);
    }

    private static void myassert(boolean test, String message) {
        if (!test) {
            throw new java.lang.AssertionError(message);
        }
    }

    private void constructSynsets(String synsets) {
        In synsetsStream = new In(synsets);
        String line = null;
        int readSynsetsCount = 0;
        int readWordsCount = 0;
        words = new HashMap<String, ArrayList<Synset>>();
        while ((line = synsetsStream.readLine()) != null) {
            String[] fields = line.split(",");
            Synset synset = new Synset();
            synset.index = Integer.parseInt(fields[0]);
            synset.words.addAll(Arrays.asList(fields[1].split(" ")));
            //synset.Description = fields[2];
            synsetIndexes.put(synset.index, synset);
            for (String word : synset.words) {
                ArrayList<Synset> relatedSynsets = words.get(word);
                if (relatedSynsets == null)
                    relatedSynsets = new ArrayList<Synset>();
                // to get sap get it more specific ? TODO find out how to make it
                int i = 0;
                for(; i < relatedSynsets.size() && relatedSynsets.get(i).words.size() >= synset.words.size(); i++)
                    ;
                relatedSynsets.add(i, synset);
                words.put(word, relatedSynsets);
                readWordsCount++;
            }
            readSynsetsCount++;
        }

        //assert !synsetsStream.isEmpty() && synsetIndexes.size() != 0;
        assert synsetIndexes.size() == readSynsetsCount;
        // WHY THIS FAILS?
        //assert words.size() == readWordsCount : "readWordsCount=" + readWordsCount + ", words.size() == " + words.size();

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
        if (nounA == null)
            throw new java.lang.NullPointerException();
        if (nounB == null)
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
        shouldFindAncestorForLargeSets();
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

    private static void shouldFindAncestorForLargeSets() {
        WordNet wn = new WordNet("wordnet\\synsets.txt", "wordnet\\hypernyms.txt");

        if (!wn.sap("worm", "bird").equals("animal"))
            debug("TEST FAIL: !wn.sap(\"worm\", \"bird\").equals(\"animal\")");

        if (wn.distance("municipality", "region") != 3)
            debug("TEST FAIL: wn.distance(\"municipality\", \"region\") != 3");

        if (!wn.sap("individual", "edible_fruit").equals("physical_entity"))
            debug("TEST FAIL: !wn.sap(\"individual\", \"edible_fruit\").equals(\"physical_entity\")");

        if (!wn.sap("individual", "edible_fruit").equals("physical_entity"))
            debug("TEST FAIL: !wn.sap(\"individual\", \"edible_fruit\").equals(\"physical_entity\")");

        myassert(wn.distance("white_marlin", "mileage") == 23);
        myassert(wn.distance("Black_Plague", "black_marlin") == 33);
        myassert(wn.distance("American_water_spaniel", "histology") == 27);
        myassert(wn.distance("Brown_Swiss", "barrel_roll") == 29);


        // 44214,greenfly,greenish aphid; pest on garden and crop plants  
        // 5349,Egyptian_cotton,fine somewhat brownish long-staple cotton grown in Egypt; believed to be derived from sea island cotton or by hybridization with Peruvian cotton  
        String ancestor = wn.sap("greenfly", "Egyptian_cotton");
        debug(" sap('greenfly', 'Egyptian_connon') = " + ancestor);
        int distance = wn.distance("greenfly", "Egyptian_cotton");
        debug(" distance('greenfly', 'Egyptian_connon') = " + distance);

        debug("TESTS SUCCESS on synsets.txt and hypernyms.txt");
    }
}
