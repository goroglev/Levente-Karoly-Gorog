package com.avg.app_similarity.rwr.graph;

import java.util.Map;

public class AppNode extends Node {
	
	public AppNode(final String id, final String packageName, final Map<Node, Double> features) {
		super(id, features);
		this.packageName = packageName;
	}
	
	public final String packageName;
	
}
