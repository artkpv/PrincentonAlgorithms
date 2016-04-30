public class Outcast {
	WordNet _wordNet;
	public Outcast(WordNet wordnet)         // constructor takes a WordNet object
	{
		_wordNet = wordnet;
	}

	public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
	{
		String outcast = null;
		int outcastDistance = -1;
		for(String noun : nouns) {
			int nounDistance = 0;
			
			for(String noun2 : nouns) { 
				if(noun2 != noun)
					nounDistance += _wordNet.distance(noun, noun2);
			}
			if(nounDistance > outcastDistance){ 
				outcast = noun;
				outcastDistance = nounDistance;
			}
		}

		return outcast;
	}

	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for (int t = 2; t < args.length; t++) {
			In in = new In(args[t]);
			String[] nouns = in.readAllStrings();
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}
	}
}
