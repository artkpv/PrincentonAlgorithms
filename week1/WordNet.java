// https://class.coursera.org/algs4partII-007/assignment/view?assignment_id=5
//

import java.util.*;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;

public class WordNet {

	class Synset {
		int Index;
		List<String> Words = new ArrayList<String>();
		String Description;
	}

	HashMap<String, ArrayList<Synset>> _words = new HashMap<String, ArrayList<Synset>>();
	HashMap<Integer, Synset> _synsetIndexes = new HashMap<Integer, Synset>();
	Digraph _digraph = null;

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms)
   {
	   if(synsets == null)
		   throw new java.lang.NullPointerException();
	   if(hypernyms == null)
		   throw new java.lang.NullPointerException();

	   System.out.println(" (DEBUG) WordNet('" + synsets + "', '" + hypernyms + "')");
	   In synsetsStream = new In(synsets);
	   String line = null;
	   int readSynsetsCount = 0;
	   int readWordsCount = 0;
	   while((line = synsetsStream.readLine()) != null ) {
		   String[] fields = line.split(",");
		   Synset synset = new Synset();
		   synset.Index = Integer.parseInt(fields[0]);
		   synset.Words.addAll(Arrays.asList(fields[1].split(" ")));
		   synset.Description = fields[2];
		   _synsetIndexes.put(synset.Index, synset);
		   for(String word : synset.Words) {
			   ArrayList<Synset> relatedSynsets = _words.get(word);
			   relatedSynsets = relatedSynsets != null ? relatedSynsets : new ArrayList<Synset>();
			   
			   relatedSynsets.add(synset);

			   _words.put(word, relatedSynsets);

			   readWordsCount++;
		   }
		   readSynsetsCount++;
	   }

	   assert !synsetsStream.isEmpty() && _synsetIndexes.size() != 0;
	   assert _synsetIndexes.size() == readSynsetsCount;
	   assert _words.size() == readWordsCount;
	   
	   System.out.println(" (DEBUG) _synsetIndexes.size() == " + _synsetIndexes.size());
	   System.out.println(" (DEBUG) _words.size() == " + _words.size());

	   // read hypernyms file
	   //  1. read to a list of lists
	   In hypernymsStream = new In(hypernyms);
	   ArrayList<ArrayList<Integer>> hypernymsLists = new ArrayList<ArrayList<Integer>>();
	   while((line = hypernymsStream.readLine()) != null) {
		   String[] fields = line.split(",");
		   ArrayList<Integer> parsed = new ArrayList<Integer>(fields.length);
		   for(String field : fields) {
			   parsed.add(Integer.parseInt(field));
		   }
		   hypernymsLists.add(parsed);
	   }
	   assert !hypernymsStream.isEmpty() && hypernymsLists.size() != 0;

	   System.out.println(" (DEBUG) hypernymsLists.size() == " + hypernymsLists.size());

	   //  2. load into algs4.Digraph
	   _digraph = new Digraph(_synsetIndexes.size());
	   ArrayList<Integer> possibleRoots	 = new ArrayList<Integer>();
	   for(List<Integer> set : hypernymsLists) {
		   int v = set.get(0);
		   int indexOfRoot = possibleRoots.indexOf(v);
		   if(indexOfRoot != -1)
			   possibleRoots.remove(indexOfRoot);
		   for(int i = 1; i < set.size(); i++) {
			   int w = set.get(i);
			   _digraph.addEdge(v, w);
			   if(_digraph.outdegree(w) == 0)
				   possibleRoots.add(w);
		   }
	   }

	   // 3. Check that it is rooted DAG
	   int numberOfRootsFound = 0;
	   for(Integer r : possibleRoots) {
		   if(_digraph.outdegree(r) == 0) {
			   numberOfRootsFound++;
			   if(numberOfRootsFound > 1) 
				   throw new java.lang.IllegalArgumentException("Unexpected number of roots");
		   }
	   }
	   System.out.println(" (DEBUG) _digraph.V() == " + _digraph.V());
	   System.out.println(" (DEBUG) _digraph.E() == " + _digraph.E());

	   System.out.println(" (DEBUG) WordNet constructed");
   }

   // returns all WordNet nouns
   public Iterable<String> nouns()
   { 
	   return _words.keySet();
   }

   // is the word a WordNet noun?
   public boolean isNoun(String word)
   { 
	   if(word == null)
		   throw new java.lang.NullPointerException();
	   return _words.containsKey(word);
   }

   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB)
   { 
	   if(nounA == null || !isNoun(nounA))
		   throw new java.lang.NullPointerException();
	   if(nounB == null || !isNoun(nounB))
		   throw new java.lang.NullPointerException();
	   if(!isNoun(nounA))
		   throw new java.lang.IllegalArgumentException();
	   if(!isNoun(nounB))
		   throw new java.lang.IllegalArgumentException();

	   SAP sap = new SAP(_digraph);
	   Synset v = getShortestSynset(nounA);
	   Synset w = getShortestSynset(nounB);
	   return sap.length(v.Index, w.Index);
   }
   
   private Synset getShortestSynset(String word) {
	   ArrayList<Synset> found = _words.get(word);
	   int min = Integer.MAX_VALUE;
	   Synset shortest = null;
	   for(Synset s : found) {
		   if(s.Words.size() < min) {
			   min = s.Words.size();
			   shortest = s;
		   }
	   }

	   return shortest;
   }

   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB)
   { 
	   if(nounA == null)
		   throw new java.lang.NullPointerException();
	   if(nounB == null)
		   throw new java.lang.NullPointerException();
	   if(!isNoun(nounA))
		   throw new java.lang.IllegalArgumentException();
	   if(!isNoun(nounB))
		   throw new java.lang.IllegalArgumentException();
	   
	   SAP sap = new SAP(_digraph);
	   Synset v = getShortestSynset(nounA);
	   Synset w = getShortestSynset(nounB);
	   int a = sap.ancestor(v.Index, w.Index);
	   if(a != -1)
		   return _synsetIndexes.get(a).Words.get(0);
	   return null;
   }

   // do unit testing of this class
   public static void main(String[] args) {
	   should_throw_if_not_rooted_DAG();
	   should_throw_if_not_rooted_DAG_cicle();
	   should_ancestor_for_worm_and_bird();
   }

   private static void should_throw_if_not_rooted_DAG(){
	   boolean isSuccess = false;
	   try {
		   WordNet wn = new WordNet("wordnet\\synsets3.txt", "wordnet\\hypernyms3InvalidTwoRoots.txt");
	   }
	   catch (java.lang.IllegalArgumentException e ) {
		   isSuccess = true;
	   }
	   if(isSuccess)
		   System.out.println("should_throw_if_not_rooted_DAG SUCCESS");
	   else
		   System.out.println("should_throw_if_not_rooted_DAG FAIL");
   }

   private static void should_throw_if_not_rooted_DAG_cicle(){
	   boolean isSuccess = false;
	   try {
		   WordNet wn = new WordNet("wordnet\\synsets3.txt", "wordnet\\hypernyms3InvalidCycle.txt");
	   }
	   catch (java.lang.IllegalArgumentException e ) {
		   isSuccess = true;
	   }
	   if(isSuccess)
		   System.out.println("should_throw_if_not_rooted_DAG_cicle SUCCESS");
	   else
		   System.out.println("should_throw_if_not_rooted_DAG_cicle FAIL");
   }

   private static void should_ancestor_for_worm_and_bird(){
	   WordNet wn = new WordNet("wordnet\\synsets.txt", "wordnet\\hypernyms.txt");
	   String ancestor = wn.sap("worm", "bird");
	   String expected = "animal";
	   if(ancestor == expected)
		   System.out.println("should_ancestor_for_worm_and_bird SUCCESS");
	   else
		   System.out.println("should_ancestor_for_worm_and_bird FAIL: " + expected + " != " + ancestor);
   }
}
