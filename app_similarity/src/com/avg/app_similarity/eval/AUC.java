package com.avg.app_similarity.eval;

import java.io.File;
import java.util.NavigableMap;
import java.util.Set;

public class AUC extends Cost {	
	
	public AUC(File goldStandardFile) throws Exception {
		super(goldStandardFile);
	}
	
	public double score(NavigableMap<String, Double> rankedMap) {
		String id = rankedMap.lastKey();		
		Set<String> hits = goldStandard.get(id);
		Set<String> results = rankedMap.headMap(id, false).keySet();
		double rank = 0;
		double rankSum = 0;
		int nrTruePos = 0;
		for (String candidate : results) {
			++rank;
			if (hits.contains(candidate)) {
				// System.out.println(candidate + ": " + rank);
				rankSum += rank;
				++nrTruePos;
			}
			if (nrTruePos == hits.size()) break;
		}
		return (rankSum - nrTruePos * (nrTruePos + 1) / 2) / ((results.size() - nrTruePos) * nrTruePos); 
	}

}
