package com.avg.app_similarity.eval;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.NavigableMap;

import com.avg.app_similarity.util.Reader;

public abstract class Cost {
	public HashMap<String, LinkedHashSet<String>> goldStandard;
	
	// comma-separated lines from a file, split by ',' and put into a hashmap (first element as key, and the *top* next as a hashset)
	public Cost(File goldStandardFile, int top) throws Exception {
		goldStandard = Reader.readMapSet(goldStandardFile, ",", top);
	}
	
	// comma-separated lines from a file, split by ',' and put into a hashmap (first element as key, and the rest as a hashset)
	public Cost(File goldStandardFile) throws Exception {
		goldStandard = Reader.readMapSet(goldStandardFile, ",", Integer.MAX_VALUE);
	}
		
	// between 0 and 1, 1 denoting perfect accuracy according to the measure used
	public abstract double score(NavigableMap<String, Double> rankedMap);
	
	// between 0 and 1, 0 denoting perfect accuracy according to the measure used
	public double cost(NavigableMap<String, Double> rankedMap) {
		return 1 - score(rankedMap);
	}

}
