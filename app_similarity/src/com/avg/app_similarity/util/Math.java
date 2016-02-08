package com.avg.app_similarity.util;

import java.util.Collection;

public class Math {
	public static double sd(Collection<Double> seq) {
		double mean = mean(seq);
		double var = 0.0;
		for (Double elem : seq) {
			var += java.lang.Math.pow(elem - mean, 2);
		}
		return java.lang.Math.sqrt(var / seq.size());		
	}
	
	
	public static double mean(Collection<Double> seq) {
		double avg = 0.;
		for (Double elem : seq) avg += elem;
		return avg / seq.size();
	}
}
