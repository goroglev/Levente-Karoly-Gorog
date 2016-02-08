/*******************************************************************************
 * Copyright 2014 Levente Gorog based on the {@link AverageLinkageStrategy} class by Lars Behnke 
 ******************************************************************************/

package com.apporiented.algorithm.clustering;

import java.util.Collection;
import java.util.Collections;

public class MaxLinkageStrategy implements LinkageStrategy {

	@Override
	public Double calculateDistance(Collection<Double> distances) {
		return distances.size() == 0 ? .0 : Collections.max(distances);
	}
}
