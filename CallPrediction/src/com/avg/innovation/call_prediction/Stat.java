package com.avg.innovation.call_prediction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Stat {
	
	
	public static Collection<Double> normalizeBetween0And1(Collection<Double> collection) {
		List<Double> normalizedCollection = new ArrayList<Double>();
		if (collection == null || collection.isEmpty()) return normalizedCollection;

		double min = Collections.min(collection);
		double range = Collections.max(collection) - min;
		if (range != .0) {				
			for (Double elem : collection) {
				normalizedCollection.add((elem-min)/range);
			}
		}
		return normalizedCollection;
	}
	
	// sse - sum of square errors
	public static double sse(Collection<Double> collection) {
		double mean = mean(collection);
		double var = 0.0;
		for (Double elem : collection) {
			var += java.lang.Math.pow(elem - mean, 2);
		}
		return var;		
	}
	
	public static double sd(Collection<Double> collection) {
		return java.lang.Math.sqrt(sse(collection) / collection.size() );
	}
	
	public static double mean(Collection<Double> collection) {
		double avg = 0.;
		for (Double elem : collection) avg += elem;
		return avg / collection.size();
	}
}
