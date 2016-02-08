package com.avg.innovation.call_prediction;

import java.util.Comparator;
import java.util.Map;

/**
 * Retrieves 2 keys from a map and compares the corresponding values.
 * Used to sort map entries by value. Are there better, simpler ways to do it?
 * 
 * @author adminuser
 *
 * @param <K>
 * @param <V>
 */
public class ValueComparator<K, V extends Comparable<? super V>> implements Comparator<K> {


	Map<K, V> base;
	
	public ValueComparator(Map<K, V> base) {
		this.base = base;
	}
	
	public int compare(K k1, K k2) {
		V v1 = base.get(k1);
		V v2 = base.get(k2);
		if (v1 != null && v2 != null && v1.compareTo(v2) > 0) return 1;
		return -1;
	}
	

}
