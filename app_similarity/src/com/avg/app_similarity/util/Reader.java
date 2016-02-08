package com.avg.app_similarity.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class Reader {
	
	// reads lines from a file, splits them into two by the split regexp and put them as casted (key, value) into a parametric-type HashMap 
	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> readIntoHashMap(File file, String splitRegexp, HashMap<K, V> _hashMap, Brain<V> brain) throws Exception {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
			final HashMap<K, V> hashMap = new HashMap<K, V>();
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] splits = line.split(splitRegexp);
				if (splits.length != 2) throw new Exception("File '" + file.getCanonicalPath() + "' contains an illegal line: '" + line + "'");
				V res = brain.compute(splits[0], splits[1]);
				if (res != null) hashMap.put((K) splits[0], res);
			}
			return hashMap;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	public static String readLine(File file, int lineNumber) throws Exception {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
			String line = null;
			for (int i = 0; i < lineNumber && ((line =input.readLine()) != null); ++i);
			return line;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	public static String readIntoString(File file, int nrLinesToSkip) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
			String line = null;
			for (int i = 0; i < nrLinesToSkip; ++i) input.readLine();
			while ((line = input.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	public static ArrayList<String> readLinesIntoList(File file, int nrLines) throws Exception {
		
		ArrayList<String> lines = new ArrayList<String>();
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null && lines.size() < nrLines) {
				lines.add(line);
			}
			return lines;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	// reads lines from a file, splits them by `splitRegexp` and puts into a hashmap the first element as key, and the rest as a hashset 	
	public static HashMap<String, LinkedHashSet<String>> readMapSet(File file, String splitRegexp, int maxElementsPerLine) throws Exception {
		
		HashMap<String, LinkedHashSet<String>> indexedSets= new HashMap<String, LinkedHashSet<String>>(); 
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] words = line.split(splitRegexp);
				List<String> list = Arrays.asList(words);
				String key = list.get(0);
				int lastIndex = (maxElementsPerLine  + 1) < list.size() ? (maxElementsPerLine + 1) : list.size(); 
				indexedSets.put(key, new LinkedHashSet<String>(list.subList(1, lastIndex)));
			}
			return indexedSets;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	// reads lines from a file, splits them by `splitRegexp` and puts into a hashmap the first element as key, and the rest as a hashset 	
	public static HashMap<String, String> readMap(File file, String splitRegexp) throws Exception {
		
		HashMap<String, String> map= new HashMap<String, String>(); 
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] words = line.split(splitRegexp);
				map.put(words[0], words[1]);
			}
			return map;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}	
	
}
