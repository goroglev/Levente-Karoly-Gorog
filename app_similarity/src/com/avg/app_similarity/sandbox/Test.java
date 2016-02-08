package com.avg.app_similarity.sandbox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.omg.CORBA.FREE_MEM;

import com.avg.app_similarity.rwr.graph.Node;
import com.avg.app_similarity.rwr.graph.TermNode;
import com.avg.app_similarity.tokenization.Stemmer;
import com.avg.app_similarity.util.Reader;
import com.avg.app_similarity.util.ValueComparator;

class Freq {
	long wordFreq;
	int wordDocFreq;
	
	public Freq(long wordFreq, int wordDocFreq) {
		this.wordFreq = wordFreq;
		this.wordDocFreq = wordDocFreq;
	}
	
	void incrWordDocFreq() {
		++wordDocFreq;
	}
	
	void addToWordFreq(int wordFreqInDoc) {
		wordFreq += wordFreqInDoc;
	}
};

enum Output {
	NONE, TOKENIZED, WORDFREQ;
};

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		Stemmer stemmer = new Stemmer(new File("../data/inv-doc-freq/stems"));
		System.out.println(stemmer.stem("easily"));
		String stem = "play".replaceFirst("y$", "i");
		System.out.println(stem);
		int a = 6;
		a /= 4; 
		System.out.println(a);
		
		Node node = null;
		node.getFeatures(TermNode.class);
		System.out.println(Math.pow(27, 1./3));
		String token = "card.(*os2.2";
		String[] words = token.split("(/)|(\\.{2,})|([()])|([*>]+)");		
		String word = token.replaceAll("^[^a-z0-9]+", "").replaceAll("[`;\\.?:'\\]\\*\\+>-]+$", "");
		System.out.println(word);
		System.exit(1);
		
		ArrayDeque<Integer> test = new ArrayDeque<Integer>();
		test.add(12); test.add(13);
		System.out.println(test.poll());
		System.out.println(test.poll());
		
		System.out.println(2.0/1l);
		String content = Reader.readIntoString(new File("../AppDescriptions/9987_com.htcheng.dict.txt"), 0);
		System.out.println(content);
		
		
		File file = new File("bla/bla/bla.txt");
		System.out.println(file.getParent());
		
		Map<String, Freq> bla = new HashMap<String, Freq>() {
			{
				put("alma", new Freq(1,2));
			}
		};
		
		Freq f = bla.get("alma");
		f.incrWordDocFreq();
		
		System.out.println(bla.get("alma").wordFreq + " " + bla.get("alma").wordDocFreq);
		System.out.println(Output.valueOf("NONE"));
		
		ValueComparator<String, Long> vcd = new ValueComparator<String, Long>(new HashMap<String, Long>());
		HashMap<String, Long> map = new HashMap<String, Long>() {
			{
				put("alma", Long.valueOf(3));
				put("alama", Long.valueOf(1));
				put("aalma", Long.valueOf(4));
			}
		};
		
		
		TreeMap<String, Long> sortedByWordDocFreq = new TreeMap<String, Long>(vcd);
	/*	sortedByWordDocFreq.put("clma", Long.valueOf(3));
		sortedByWordDocFreq.put("blama", Long.valueOf(1));
		sortedByWordDocFreq.put("aalma", Long.valueOf(9));
		sortedByWordDocFreq.put("dalma", Long.valueOf(5));
	*/	sortedByWordDocFreq.putAll(map);

		
		for (Entry<String, Long> wordFreq : sortedByWordDocFreq.descendingMap().entrySet()) {
					System.out.println(wordFreq.getKey() + "\t" + wordFreq.getValue() + "\n");
				}
		
//		if (!file.getParentFile().exists()) {
//			file.getParentFile().mkdirs();
//		}
//		FileWriter fw = new FileWriter(file);
//		fw.write("ash");
//		fw.close();
	}

}
