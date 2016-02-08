package com.avg.app_similarity.rwr.graph;

import java.util.Map;

public class TermNode extends Node {

	public double sum = 0;
	
	public TermNode(String id, Map<Node, Double> features) {
		super(id, features);
	}

}
