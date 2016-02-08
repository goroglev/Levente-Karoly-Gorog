package com.avg.app_similarity.tokenization;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.avg.app_similarity.util.Reader;

public class Stemmer {
	
	public Stemmer(File stemFile) throws Exception {
		stems = Reader.readMap(stemFile, "\t");
	}
	
	public String stem(String word) {
		String stem = stems.get(word);
		return stem == null ? word : stem;
	}

	private static Map<String, String> stem(String word, Set<String> vocabulary) {
		Map<String, String> mappings = new HashMap<String, String>();
		HashSet<String> alternatives = new HashSet<String>();		
		
		if (word.endsWith("e")) {
			alternatives.add(word.concat("s"));
			alternatives.add(word.concat("d"));
			alternatives.add(word.replaceFirst("e$", "ing"));
			alternatives.add(word.concat("ly"));
		} else if (word.matches("[aeiou]y$")) {
			alternatives.add(word.concat("s"));
			alternatives.add(word.concat("ed"));
			alternatives.add(word.concat("ing"));
			alternatives.add(word.concat("ly"));
		} else if (word.endsWith("y")) {
			String stem = word.replaceFirst("y$", "i");
			alternatives.add(stem.concat("es"));
			alternatives.add(stem.concat("ed"));
			alternatives.add(word.concat("ing"));
			alternatives.add(stem.concat("ly"));
		} else {
			alternatives.add(word.concat("s"));
			alternatives.add(word.concat("ed"));
			alternatives.add(word.concat("ing"));
			alternatives.add(word.concat("ly"));
		}
		
		for (String alternative : alternatives) {
			if (vocabulary.contains(alternative)) {
				mappings.put(alternative, word);
			}
		}
		return mappings;
	}
	
	public static void main(String[] args) throws Exception {
		HashSet<String> vocab = new HashSet<String>();
		vocab.addAll(Reader.readLinesIntoList(new File("../data/inv-doc-freq/vocab"), Integer.MAX_VALUE));
		HashMap<String, String> mappings = new HashMap<String, String>();
		for (String word : vocab) {
			if (word.length() > 3)
			mappings.putAll(stem(word, vocab));
		}
		FileWriter fw = new FileWriter("../data/inv-doc-freq/stems");
		for (Entry<String, String> vocabEntry : mappings.entrySet()) {
			fw.write(vocabEntry.getKey() + "\t" + vocabEntry.getValue() + "\n");
		}
		fw.close();
	}
	
	private final HashMap<String, String> stems;

}
