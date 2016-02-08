package com.avg.innovation.call_prediction;

import java.util.Map;
import java.util.TreeMap;

public class Sorter {
	
	/**
	 * 
	 * @param map - a generic map with comparable values
	 * @return a {@link TreeMap} with its (key, value) entries sorted by value 
	 */
	public static <K, V extends Comparable<? super V>> TreeMap<K, V> sortByValue(Map<K, V> map) {
		ValueComparator<K, V> vcd = new ValueComparator<K, V>(map);
		TreeMap<K, V> mapSortedByValue = new TreeMap<K, V>(vcd);
		mapSortedByValue.putAll(map);
		return mapSortedByValue;
	}

}
