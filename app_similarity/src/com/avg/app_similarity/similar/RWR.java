package com.avg.app_similarity.similar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NavigableMap;

import com.avg.app_similarity.rwr.graph.AppNode;
import com.avg.app_similarity.rwr.graph.Node;
import com.avg.app_similarity.util.Sorter;

public class RWR {	

	public RWR(final double convergenceThreshold, final double restartProb, final int maxIterations) {
		this.convergenceThreshold = convergenceThreshold;
		this.restartProb = restartProb;
		this.noRestartProb = 1 - restartProb;
		this.maxIterations = maxIterations;
	}	
	
	public NavigableMap<String, Double> walk(final ArrayList<ArrayList<Node>> connectedGraphs, Node startNode) {
		ArrayList<Node> graph = connectedGraphs.get(startNode.graphIndex);
		reset(graph);		
		do {
			for (Node node : graph) {
				for (Entry<Node, Double> neighbor : node.features.entrySet()) {
					neighbor.getKey().tempProb += node.prob * neighbor.getValue() * noRestartProb;
				}				
			}
			startNode.tempProb += restartProb;
			++nrIterations;
		} while (!(converges(graph, startNode)) && nrIterations < maxIterations);		
		
		HashMap<String, Double> appProbDistrib = new HashMap<String, Double>();
		for (Node node : graph) {
			if (node instanceof AppNode) {
				appProbDistrib.put(node.id, node.prob);
				//System.out.println(node.id + ": " + node.prob);
			}
		}
		return Sorter.sortByValue(appProbDistrib);
	}
	
	public int getLastNrIterations() {
		return this.nrIterations;
	}
	
	private void reset(ArrayList<Node> graph) {
		nrIterations = 0;
		for (Node node : graph) {
			node.prob = 1.0 / graph.size(); node.tempProb = 0;
		}		
	}	

	private boolean converges(Collection<Node> iteratedGraph, Node startNode) {
		boolean converges = true;
		for (Node node : iteratedGraph) {		
			if (converges) {
				converges = (Math.abs(node.tempProb - node.prob) < convergenceThreshold);
			}
			node.prob = node.tempProb; node.tempProb = 0; // update prob. distrib.
		}
		return converges;	
	}	
	
	private final double convergenceThreshold;

	private final double restartProb;
	
	private final double noRestartProb;
	
	private final int maxIterations;
	
	private int nrIterations;	
	
}
