package com.avg.app_similarity.similar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import com.avg.app_similarity.eval.RankedScoring;
import com.avg.app_similarity.rwr.graph.AppNode;
import com.avg.app_similarity.rwr.graph.CategoryNode;
import com.avg.app_similarity.rwr.graph.Node;
import com.avg.app_similarity.rwr.graph.TermNode;
import com.avg.app_similarity.util.Format;

public class Similar {
		
	public Similar(String[] arguments) throws Exception {
		args = new Arguments(arguments);
		fwTop = new FileWriter(args.similarApps);
		rwr = new RWR(args.convergenceThreshold, args.restartProb, args.maxIterations);
	}
	
	public void process() throws Exception {
		buildGraph();
		final ArrayList<ArrayList<Node>> connectedGraphs = createConnectedGraphs();
		
		long startTime = System.currentTimeMillis();		
		
		int nrOfAvgIterations = 0;		
		ArrayList<Double> costList = new ArrayList<Double>();
		
		FileWriter fwRank = null;
		if (args.rank) fwRank = new FileWriter(args.rankFile);
		int i = 0;
		
		RankedScoring.avgHits = 0;
				
		for (String startNodeId : args.startNodes) {			
			Node startNode = appNodes.get(startNodeId);								
			if (startNode != null) {
				System.out.print(++i +". " + startNodeId + ", " + startNode.getFeatures(CategoryNode.class).firstKey().id + ", cost=");
				NavigableMap<String, Double> similarApps = rwr.walk(connectedGraphs, startNode);				
				if (args.cost != null && args.cost.goldStandard.containsKey(startNode.id)) {
					double cost = args.cost.cost(similarApps);
					System.out.println(Format.df3.format(cost) + ", #iterations=" + rwr.getLastNrIterations());
					costList.add(cost);
				} else System.out.println();
				int j=0; int k=0;
				for (Entry<String, Double> similarApp : similarApps.descendingMap().entrySet()) {
					if (j++ < args.top) {
						fwTop.write(similarApp.getKey() + " ");
						if (args.probDistrib) fwTop.write(similarApp.getValue() + " ");
					}
					if (args.rank && k < args.startNodes.size() && args.startNodes.contains(similarApp.getKey())) {
						++k; fwRank.write(similarApp.getKey() + " ");
					}
					if ((j >= args.top && !args.rank) || (k >= args.startNodes.size()) ) break;
				}
			} else {
				fwTop.write(startNodeId + " does not exist in the graph!");
				System.out.println(startNodeId + " does not exist in the graph!");
			}
			fwTop.write("\n");
			fwTop.flush();
			if (args.rank) {
				fwRank.write("\n");
				fwRank.flush();
			}
		}		

		long stopTime = System.currentTimeMillis();
		nrOfAvgIterations /= args.startNodes.size();
		fwTop.write("Elapsed time (sec): " + (stopTime - startTime) / 1000. + "\n");
		fwTop.write("Nr. of avg iterations: " + nrOfAvgIterations + "\n");
		nrOfAvgIterations = 0; 
		fwTop.write("Mean cost: " + com.avg.app_similarity.util.Math.mean(costList) + "\n");
		fwTop.write("sd cost: " + com.avg.app_similarity.util.Math.sd(costList) + "\n");
		fwTop.write(RankedScoring.avgHits);
		fwTop.close();
		if (fwRank != null) fwRank.close();
	}

	public void train() throws IOException {
		buildGraph();
		final ArrayList<ArrayList<Node>> connectedGraphs = createConnectedGraphs();
		
		double featureW = args.featureWeights.get(1);
		for (double weight = 0.3; weight < 0.6; weight += .05) {
			RankedScoring.avgHits = 0;
			changeWeights(weight, featureW);
			featureW = weight;
			long startTime = System.currentTimeMillis();
			
			int nrOfAvgIterations = 0;
			
			ArrayList<Double> costList = new ArrayList<Double>();
			double totalCost = 0.0;				
			
			for (String startNodeId : args.startNodes) {			
				Node startNode = appNodes.get(startNodeId);								
				if (startNode != null) {
					NavigableMap<String, Double> similarApps = rwr.walk(connectedGraphs, startNode);
					nrOfAvgIterations += rwr.getLastNrIterations();
					if (args.cost != null && args.cost.goldStandard.containsKey(startNode.id)) {
						double cost = args.cost.cost(similarApps);
						// System.out.println(startNode.getFeatures(CategoryNode.class).firstKey().id + "\t" + cost);
						costList.add(cost);
						totalCost += cost;
					}
				} else {
					System.out.println(startNodeId + " does not exist in the graph!");
					System.exit(-1);
				}
			}		
			
			double meanCost = totalCost / costList.size();
			double var = 0.0;
			for (Double cost : costList) {
				var += Math.pow(cost - meanCost, 2);
			}
			double sd = Math.sqrt(var / costList.size());
			
			long stopTime = System.currentTimeMillis();
			nrOfAvgIterations /= args.startNodes.size();
			
			System.out.println("[" + Format.df3.format(1-weight) + "," + Format.df3.format(weight) + "], cost=" + Format.df3.format(meanCost) + ";sd=" + Format.df3.format(sd) + " (" + args.cost.getClass().getSimpleName() + "), avg. #iterations=" + nrOfAvgIterations + ", sample size=" + args.startNodes.size() + ", elapsed time=" + (stopTime - startTime) / 1000. + " sec\n");
			System.out.println(RankedScoring.avgHits);
		}
}
	
	public static void main(final String[] args) throws Exception {
		Similar similar = new Similar(args);
//		similar.train();
		similar.process();
	}
	
	private void buildGraph() throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(args.appKeywordFile));
		String line = null;
		int i = 0;
		while ((line = input.readLine()) != null && i < args.max) {
			String[] appInfo = line.split("\t");
			final Map<Node, Double> features = new HashMap<Node, Double>();			
			if (appInfo.length == 5) {
				AppNode appNode = new AppNode(appInfo[0], appInfo[1], features);
				CategoryNode categoryNode = categoryNodes.get(appInfo[2]);
				if (categoryNode == null) {
					categoryNode = new CategoryNode(appInfo[2], new HashMap<Node, Double>());
					categoryNodes.put(appInfo[2], categoryNode);
				}
				categoryNode.features.put(appNode, 0.);
				appNode.features.put(categoryNode, args.featureWeights.get(0));
							
				String[] weightedKeywords = appInfo[4].split(" ");
				for (int j = 0; j < weightedKeywords.length; j += 3) {
					String keyword = weightedKeywords[j];
					TermNode termNode = termNodes.get(keyword);
					if (termNode == null) {
						termNode = new TermNode(keyword, new HashMap<Node, Double>());
						termNodes.put(keyword, termNode);
					}
					Double appTermWeight = Double.valueOf(weightedKeywords[j+1]) * args.featureWeights.get(1);
					Double termAppWeight = Double.valueOf(weightedKeywords[j+2]);
					termNode.features.put(appNode, termAppWeight);
					termNode.sum += termAppWeight;
					appNode.features.put(termNode, appTermWeight);
					appNodes.put(appInfo[0], appNode);
				}
				++i;
			} else {
				fwTop.write("Feature selection\t" + line + "\n");
			}
		}
		// compute the term => app transition prob.
		for (TermNode termNode : termNodes.values()) {
			for (Entry<Node, Double> appNodeEntry : termNode.features.entrySet()) {
				appNodeEntry.setValue(appNodeEntry.getValue() / termNode.sum);
			}
		}
		// compute the category => app transition prob.
		for (CategoryNode categoryNode : categoryNodes.values()) {
			for (Entry<Node, Double> appNodeEntry : categoryNode.features.entrySet()) {
				appNodeEntry.setValue(1. / categoryNode.features.size());
			}
		}

		fwTop.write("RWR params\tConvergence threshold: " + args.convergenceThreshold + "\n");
		fwTop.write("RWR params\tMax number of iterations: " + args.maxIterations + "\n");
		fwTop.write("RWR params\tRestart probability: " + args.restartProb + "\n");
		fwTop.write("RWR params\tNr. of top similar apps: " + args.top + "\n");
		fwTop.write("RWR params\tFeature weigths (category, content rating, terms): " + Arrays.toString(args.featureWeights.toArray()) +  "\n");

		String buildGraphCompletedMsg = "Building graph successfully completed. " + appNodes.size() + " app nodes, " + termNodes.size() + " term nodes, " 
				+ categoryNodes.size() + " category nodes.";
		System.out.println(buildGraphCompletedMsg);
		fwTop.write(buildGraphCompletedMsg + "\n");
		input.close();
		fwTop.flush();
	}
	
	private final void changeWeights(double termsW, double previousTermsW) {
		double categoryW = 1 - termsW;
		for (AppNode appNode : appNodes.values()) {
			for (Entry<Node, Double> connection : appNode.features.entrySet()) {
				if (connection.getKey() instanceof CategoryNode) connection.setValue(categoryW);
				else connection.setValue(termsW * connection.getValue() / previousTermsW);
			}
		}
	}
	
	private final ArrayList<ArrayList<Node>> createConnectedGraphs() throws IOException {
		final ArrayList<ArrayList<Node>> connectedGraphs = new ArrayList<ArrayList<Node>>();
		
		Collection<AppNode> undiscoveredApps = new HashSet<AppNode>(appNodes.values());
		while (undiscoveredApps.size() > 0) {
			Node start = undiscoveredApps.iterator().next();
			ArrayList<Node> connectedGraph = new ArrayList<Node>();	
			start.graphIndex = connectedGraphs.size();
			connectedGraph.add(start);
			int j = 0;
			while (j < connectedGraph.size()) {
				Node current = connectedGraph.get(j);
				for (Node neighbor : current.features.keySet()) {
					if (!connectedGraph.contains(neighbor)) {
						neighbor.graphIndex = connectedGraphs.size();
						connectedGraph.add(neighbor);
					}
				}
				++j;
			}
			connectedGraphs.add(connectedGraph);
			undiscoveredApps.removeAll(connectedGraph);
		}
		System.out.println(connectedGraphs.size() + " connected graph(s).");
		fwTop.write(connectedGraphs.size() + " connected graph(s).\n");
		fwTop.flush();
		
		return connectedGraphs;
	}
	
	private final RWR rwr;
	
	private final HashMap<String, AppNode> appNodes = new HashMap<String, AppNode>();
	
	private final HashMap<String, TermNode> termNodes = new HashMap<String, TermNode>();
	
	private final HashMap<String, CategoryNode> categoryNodes = new HashMap<String, CategoryNode>();
	
	private final FileWriter fwTop;

	private final Arguments args;
	
}