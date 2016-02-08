package com.avg.innovation.call_prediction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Frequency and probability of a call (given a criteria which is not transparent here)
 *  
 * @author adminuser
 *
 */
class FreqProb {
	public Integer freq;
	public Double prob;
	
	public FreqProb(Integer freq, Double prob) {
		this.freq = freq;
		this.prob = prob;
	}
};


/**
 * Essential information about a cluster of calls.
 * @author adminuser
 *
 */
class ClusterStats {	
//	public double median;
	public double mean;
	public double sd;
	public double min; // which second of the day the cluster starts with
	public double max; // which second of the day the cluster ends with
	public double maxDist; // max distance between the two most far-away elements in the cluster (in sec)
	// key is the last or the last two calls (incoming/outgoing) before the current call (key in the Map<String, FreqProb>)
	public TreeMap<String, TreeMap<String, FreqProb>> markovProb = new TreeMap<String, TreeMap<String, FreqProb>>(); 	
};


public class CallStats {


	/**
	 * reads cluster information from file and stores them in cluster statistics objects
	 * @param clusterFileName
	 * @throws IOException
	 */
	
	public CallStats(String clusterFileName) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(clusterFileName));
			String line = reader.readLine();
			if (line == null) throw new IOException(clusterFileName + " is empty!");
			int noClusters = Integer.parseInt(line);
			clusterStats = new TreeMap<Double, ClusterStats>();
			for (int i = 0; i < noClusters; ++i) {
				line = reader.readLine();
				String[] parts = line.split(" ");
				ClusterStats cs = new ClusterStats();
				cs.min = Double.parseDouble(parts[0]);
				cs.mean = Double.parseDouble(parts[1]);
				cs.sd = Double.parseDouble(parts[2]);
				cs.max = Double.parseDouble(parts[3]);
				cs.maxDist = Double.parseDouble(parts[4]);
				int noPrevStates = Integer.parseInt(parts[5]);
				for (int j = 0; j < noPrevStates; ++j) {
					line = reader.readLine();
					parts = line.split(" ");
					String key  = parts[0];
					int noFollowStates = Integer.parseInt(parts[1]);
					TreeMap<String, FreqProb> callStats = new TreeMap<String, FreqProb>();
					for (int k = 0; k < noFollowStates; ++k) {
						line = reader.readLine();
						parts = line.split(" ");
						callStats.put(parts[0], new FreqProb(Integer.valueOf(parts[1]), Double.valueOf(parts[2])));
					}
					cs.markovProb.put(key, callStats);
				}
				clusterStats.put(cs.min, cs);
			}
		} finally {
			if (reader != null) reader.close();
		}
	}

	public TreeMap<Double, ClusterStats> getClusterProbabilities(int secIn2day) {
		final TreeMap<Double, ClusterStats> clustProbs = new TreeMap<Double, ClusterStats>();
		for (ClusterStats cs : this.clusterStats.values()) {
			int secIn2dayAdj = secIn2day;
			if (Math.abs(cs.mean - secIn2dayAdj) >= Math.abs(Math.abs(cs.mean - secIn2dayAdj) - CalendarUtils.secondsInADay)) {
				if (cs.mean < secIn2dayAdj) {
					secIn2dayAdj = secIn2dayAdj - CalendarUtils.secondsInADay;					
				} else {
					secIn2dayAdj = secIn2dayAdj + CalendarUtils.secondsInADay;
				}
			}
			double diff = cs.mean -  secIn2dayAdj;
			NormalDistribution nd = new NormalDistribution(cs.mean, cs.sd);			
			double prob = diff > 0 ? nd.cumulativeProbability(secIn2dayAdj) : 1 - nd.cumulativeProbability(secIn2dayAdj);
			clustProbs.put(prob, cs);
		}
		return clustProbs;
	}
	
	public static void printClusterStats(final TreeMap<Double, ClusterStats> clusterStats, String fileName) throws FileNotFoundException {
		PrintStream ps = new PrintStream(fileName);
		ps.println(clusterStats.size());
		for (ClusterStats cs : clusterStats.values()) {
			ps.print(cs.min); ps.print(" "); ps.print(cs.mean); ps.print(" ");  ps.print(cs.sd); ps.print(" "); ps.print(cs.max); ps.print(" ");
			ps.print(cs.maxDist); ps.print(" "); ps.println(cs.markovProb.size());
			for (Entry<String, TreeMap<String, FreqProb>> statsEntry : cs.markovProb.entrySet()) {
				ps.append(statsEntry.getKey()).append(" "); ps.println(statsEntry.getValue().size());
				for (Entry<String, FreqProb> callProbEntry : statsEntry.getValue().entrySet()) {
					ps.append(callProbEntry.getKey()).append(" "); ps.print(callProbEntry.getValue().freq); ps.print(" "); ps.println(callProbEntry.getValue().prob);
				}
			}
		}
		ps.close();
	}
	
	// for debugging only
	public static void printClusterStats(final TreeMap<Double, ClusterStats> clusterStats) {
		for (ClusterStats cs : clusterStats.values()) {
			System.out.println(CalendarUtils.timeOfDay((int)cs.min) + "\t[" + CalendarUtils.timeOfDay((int)cs.mean) + "]\t" +  CalendarUtils.timeOfDay((int)cs.max)); // + "\t-" + cs.maxDist / 3600.);
			for (Entry<String, TreeMap<String, FreqProb>> statsEntry : cs.markovProb.entrySet()) {
				System.out.println(statsEntry.getKey());
				for (Entry<String, FreqProb> callProbEntry : statsEntry.getValue().entrySet()) {
					System.out.println("\t" + callProbEntry.getKey() + " [" + callProbEntry.getValue().freq + ", " + callProbEntry.getValue().prob + "]");
				}
			}
		}
	}
		
	public final TreeMap<Double, ClusterStats> clusterStats;
}