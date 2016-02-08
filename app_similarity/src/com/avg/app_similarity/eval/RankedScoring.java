package com.avg.app_similarity.eval;

import java.io.File;
import java.util.NavigableMap;
import java.util.Set;

/**
 * 
 * @author levente.gorog
 * 
 * Expected utility of a ranked list of items to the user.
 * see 3.1. Evaluation criteria in this paper: http://research.microsoft.com/pubs/69656/tr-98-12.pdf
 *
 */
public class RankedScoring extends Cost {

	// TODO: make these static members instance members and change the implementation accordingly.
	private static final double HALFLIFE = 4; // 5 -1
	
	private static final int nrOfResOfInterest = 8;	
	
	private static final double[] scores; 
	
	private static double[] perfectScores;
	
	// TODO: calculating the average hit / overlap probably is sub-optimal with this static member 
	public static int avgHits = 0;
	
	static {
		scores = new double[nrOfResOfInterest];
		perfectScores = new double[nrOfResOfInterest];
		
		scores[0] = 1; perfectScores[0] = 1;
		for (int i = 1; i < nrOfResOfInterest; ++i) {

			scores[i] = 1 / Math.pow(2., i/HALFLIFE);
			perfectScores[i] = perfectScores[i-1] + scores[i];
		}
	}
	
	public RankedScoring(File goldStandardFile) throws Exception {
		super(goldStandardFile, nrOfResOfInterest);
	}
	
	public double score(NavigableMap<String, Double> rankedMap) {
		
		String id = rankedMap.descendingKeySet().first();		
		Set<String> hits = goldStandard.get(id);
		if (hits == null) System.out.println(id);
		Set<String> results = rankedMap.descendingMap().tailMap(id).keySet();
		int i = -1;
		int j = 0;
		double score = .0;
		for (String candidate : results) {
			if (++i >= nrOfResOfInterest || j >= hits.size()) break;
			if (hits.contains(candidate)) {
				++j;
				score += scores[i];
			}
		}
//		System.out.println(j);
		avgHits += j;
		int index = nrOfResOfInterest > hits.size() ? hits.size() : nrOfResOfInterest;
		if (index > results.size()) index = results.size();
		return score / perfectScores[index - 1];
	}

}
