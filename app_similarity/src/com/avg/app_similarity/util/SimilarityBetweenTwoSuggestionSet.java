package com.avg.app_similarity.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.avg.app_similarity.eval.RankedScoring;

public class SimilarityBetweenTwoSuggestionSet {

	public static void main(String[] args) throws Exception {
		
		// arg 0 is the gold standard
		RankedScoring scorer = new RankedScoring(new File(args[0]));
		// arg 1 is the suggestion set which is compared to the gold standard
		HashMap<String, LinkedHashSet<String>> suggestions = Reader.readMapSet(new File(args[1]), ",", 8);
		// arg 2 is the test (common subset of arg 0 and arg 1) for which the average similarity score is calculated
		ArrayList<String> test = Reader.readLinesIntoList(new File(args[2]), Integer.MAX_VALUE);
		FileWriter fw = new FileWriter(args[3]);
		 
		List<Double> costs = new ArrayList<Double>();
		for (String testId : test) {
			HashMap<String, Double> _suggestions = new HashMap<String, Double>();
			_suggestions.put(testId, 11.);
			double i = 10.;
			for (String suggestion : suggestions.get(testId)) {
//				System.out.print(suggestion + " ");
				_suggestions.put(suggestion, i--);
			}
			double cost = scorer.cost(Sorter.sortByValue(_suggestions));
			costs.add(cost);
			fw.write(testId + "," + cost + "\n");			
		}		
		fw.write("Avg. cost=" + Math.mean(costs) + ", sd=" + Math.sd(costs) + ", avg. match (in top 10)=" + ((double)RankedScoring.avgHits) / test.size());
		fw.close();
		System.out.println("Avg. cost=" + Math.mean(costs) + ", sd=" + Math.sd(costs));		
		System.out.println(((double)RankedScoring.avgHits) / test.size());

	}

}
