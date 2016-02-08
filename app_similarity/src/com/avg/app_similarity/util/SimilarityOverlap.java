package com.avg.app_similarity.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * computes the overlap (no. of common elements) in the top 7 elements
 * of google and ubergrid suggestions 
 */
public class SimilarityOverlap {

	public static void main(String[] args) throws Exception {
		
		HashMap<String, LinkedHashSet<String>> ubergrid = Reader.readMapSet(new File(args[0]), ",", Integer.MAX_VALUE);
		HashMap<String, LinkedHashSet<String>> google = Reader.readMapSet(new File(args[1]), ",", Integer.MAX_VALUE);
		ArrayList<String> tests = Reader.readLinesIntoList(new File(args[2]), Integer.MAX_VALUE);
//		FileWriter fw = new FileWriter(args[3]);
		 
		ArrayList<Double> overlap = new ArrayList<Double>();
		for (String test : tests) {
			Set<String> gSimilar = google.get(test);
			Set<String> uSimilar = ubergrid.get(test);
			int size = 7; // gSimilar.size() < uSimilar.size() ? gSimilar.size() : uSimilar.size();
			Collection<String> gSimilars = new ArrayList<String>(gSimilar).subList(0, size);
			Collection<String> uSimilars = new ArrayList<String>(uSimilar).subList(0, size);
			gSimilars.retainAll(uSimilars);
			double similarityCoeff = ((double) gSimilars.size()) / size;
			overlap.add(similarityCoeff);
			System.out.println(similarityCoeff);
		}
		System.out.println(Math.mean(overlap) + "\t" + Math.sd(overlap));
//		fw.close();
	}

}
