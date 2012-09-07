package raxa.nlp.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class TagsReader {
	
	private BufferedReader reader;
	private String tag;
	private TreeSet<Keyword> positiveKeywords;
	private TreeSet<Keyword> negativeKeywords;
	
	private boolean verbose;
	public TagsReader(String fileName, String tag) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(fileName));
		positiveKeywords = new TreeSet<Keyword>();
		negativeKeywords = new TreeSet<Keyword>();
		this.tag = tag;
		verbose = true;
	}
	
	public void start() throws IOException {
		String sample = reader.readLine();
		int count = 0;
		while(sample != null) {
			StringTokenizer st = new StringTokenizer(sample);
			String token = st.nextToken();
			Boolean isPositive = true;
			if (token.compareToIgnoreCase(tag) == 0) {
				isPositive = true;				
			} else {
				isPositive = false;
				
			}
			count++;
			while(st.hasMoreTokens()) {
				token = st.nextToken();
				if(isPositive) {
					positiveKeywords = addExample(positiveKeywords, token);
					//echo("Adding positive keyword: " + token);
				} else {
					negativeKeywords = addExample(negativeKeywords, token);
					//echo("Adding negative keyword: " + token);
				}
			}				
			sample = reader.readLine();	
		}
		
		if(verbose) {
			printStats();
		}
	}
	
	private void echo(String msg) {
		if(verbose) {
			System.out.println("[TagsReader]" + msg );
		}
	}
	
	private void printStats() {
		echo("----------------- Printing Statistics ---------------");
		echo("Total No. of Keywords          : " + (positiveKeywords.size() + negativeKeywords.size()));
		echo("Total No. of Positive Keywords : " + positiveKeywords.size());
		echo("Total No. of Negative Keywords : " + negativeKeywords.size());
		
		conclusion();
	}
	
	public void conclusion() {
		Iterator<Keyword> iter = positiveKeywords.iterator();
		TreeSet<Keyword> truePositive = new TreeSet<TagsReader.Keyword>();
		while(iter.hasNext()) {
			Iterator<Keyword> negIter = negativeKeywords.iterator();
			Keyword keyword = iter.next();
			boolean found = false;
			while(negIter.hasNext() && !found) {
				Keyword tmp = negIter.next();
				if(tmp.getKeyword().compareToIgnoreCase(keyword.getKeyword()) == 0) {
					found = true;
				}
			}
			if(!found ) {
				truePositive.add(keyword);
			}
		}
		
		iter = truePositive.descendingIterator();
		while(iter.hasNext()) {
			Keyword tmp = iter.next();
			echo(tmp.getKeyword() + ": " + tmp.getCount());
		}
	}

	private TreeSet<Keyword> addExample(TreeSet<Keyword> set, String token) {
		TreeSet<Keyword> tmp = new TreeSet<Keyword>();
		Iterator<Keyword> iter = set.iterator();
		boolean found = false;
		
		while(iter.hasNext()) {
			Keyword next = iter.next();
			if(next.getKeyword().compareToIgnoreCase(token) == 0) {
				next.increaseCount();
				found = true;
			}
			tmp.add(next);
		}
		if(!found) {
			tmp.add(new Keyword(token.toLowerCase()));
		}
		return tmp;
	}


	public class Keyword implements Comparable<Keyword>{
		private String word;
		private int count;
		public Keyword(String word) {
			this.word = word;
			this.count = 1;
		}
		
		public int getCount() {
			return this.count;
		}
		
		public String getKeyword() {
			return this.word;
		}
		
		public void decreaseCount() {
			if(count > 0) {
				count --;
			} else {
				throw new Error("Count of a word can't be < 0");
			}
		}
		
		public void increaseCount() {
			count ++;
		}
		
		@Override
		public int compareTo(Keyword o) {
			if(count > o.getCount()) {
				return 1;
			} else if( count < o.getCount()) {
				return -1;
			} else {
				return word.compareToIgnoreCase(o.getKeyword());
			}
		}		
	}
}
