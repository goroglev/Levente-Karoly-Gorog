package com.avg.app_similarity.util;

import java.util.Map;
import java.util.TreeMap;

public class Sorter {
	
	// ? <? super V>
	public static <K, V extends Comparable<? super V>> TreeMap<K, V> sortByValue(Map<K, V> map) {
		ValueComparator<K, V> vcd = new ValueComparator<K, V>(map);
		TreeMap<K, V> mapSortedByValue = new TreeMap<K, V>(vcd);
		mapSortedByValue.putAll(map);
		return mapSortedByValue;
	}

}
