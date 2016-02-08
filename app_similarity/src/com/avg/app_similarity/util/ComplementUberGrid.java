package com.avg.app_similarity.util;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
/**
 * 
 * @author levente.gorog
 * 
 * adds 2-3 more suggestions to ubergrid's 7-8 suggestions so that suggestions will
 * have a uniform size of 10 
 *
 */
public class ComplementUberGrid {

	public static void main(String[] args) throws Exception {
		
		HashMap<String, LinkedHashSet<String>> uberGrid = Reader.readMapSet(new File(args[0]), ",", Integer.MAX_VALUE);
		HashMap<String, LinkedHashSet<String>> gSimilar = Reader.readMapSet(new File(args[1]), ",", Integer.MAX_VALUE);
		
		HashSet<String> falloutIds = new HashSet<String>();
		
		for (Entry<String, LinkedHashSet<String>> uberGridEntry : uberGrid.entrySet()) {
			LinkedHashSet<String> gSuggestions = gSimilar.get(uberGridEntry.getKey());
			if (gSuggestions == null) {
//				System.out.println(uberGridEntry.getKey());
				falloutIds.add(uberGridEntry.getKey());
				continue;
			}
			gSuggestions.removeAll(uberGridEntry.getValue());
			while (uberGridEntry.getValue().size() < 10 && gSuggestions.size() > 0) {
				String next = gSuggestions.iterator().next();
				uberGridEntry.getValue().add(next);
				gSuggestions.remove(next);
			}
			if (uberGridEntry.getValue().size() < 10) falloutIds.add(uberGridEntry.getKey());
		}
		uberGrid.keySet().removeAll(falloutIds);
		
		System.out.println(uberGrid.size());
		
		FileWriter fw = new FileWriter(args[2]);
		for (Entry<String, LinkedHashSet<String>> uberGridEntry : uberGrid.entrySet()) {
			fw.append(uberGridEntry.getKey());
			for (String similarIds : uberGridEntry.getValue()) {
				fw.append(',').append(similarIds);
			}
			fw.append('\n');
		}
		fw.close();

	}

}
