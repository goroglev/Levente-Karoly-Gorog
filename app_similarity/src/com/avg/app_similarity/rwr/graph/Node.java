package com.avg.app_similarity.rwr.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.avg.app_similarity.util.Sorter;

public abstract class Node {
	
	public Node(final String id, final Map<Node, Double> features) {
		this.id = id;
		this.features = features;
	}
	
	public TreeMap<? extends Node, Double> getFeatures(Class<? extends Node> type) {
		HashMap<Node, Double> probDistrib = new HashMap<Node, Double>();
		for (Node node : features.keySet()) {
			if (node.getClass() == type) {
				probDistrib.put(node, node.prob);
			}
		}
		return Sorter.sortByValue(probDistrib);
	}
	
	public String id; // integer id, corresponds to the db key?
	public int graphIndex; // array index to access a connected sub-graph
	public Map<Node, Double> features;
	public double prob = .0;
	public double tempProb = .0;
	public double previousProb = .0;
}
