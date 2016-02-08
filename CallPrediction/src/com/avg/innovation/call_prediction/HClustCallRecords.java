package com.avg.innovation.call_prediction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.HierarchyBuilder;
import com.apporiented.algorithm.clustering.LinkageStrategy;
import com.apporiented.algorithm.clustering.MaxLinkageStrategy;

class ClusterInfo {
	public TreeMap<Calendar, String> entries = new TreeMap<Calendar, String>(); // call entries (timestamp, phone number) in the cluster
	double maxDist; // maximal distance in seconds between the earliest and latest call time in the cluster (within a day)
}

public class HClustCallRecords extends DefaultClusteringAlgorithm {
	

	public HClustCallRecords(List<CallRecord> callRecords, LinkageStrategy linkageStrategy) {
		super();
		this.callRecords = callRecords;
		this.linkageStrategy = linkageStrategy;
	}
	
	/**
	 * clusters call entries of a list of call records into weekday- and weekend clusters and writes them to 2 separate files.  
	 * 
	 * @param callRecords
	 * @param weekClusterFileName
	 * @param weekendClusterFileName
	 * @throws Exception
	 */
	public static void clusterCallStats(final String callLogFile, final String weekClusterFileName, final String weekendClusterFileName) throws Exception {
		CallRecords callRecords = new CallRecords(callLogFile);
		final List<CallRecord> weekRecords = callRecords.daysOfWeek(true, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY);
		final List<CallRecord> weekendRecords = callRecords.daysOfWeek(true, Calendar.SATURDAY, Calendar.SUNDAY);
		@SuppressWarnings("serial")
		Map<String, List<CallRecord>> clustering = new HashMap<String, List<CallRecord>>() {
			{
				put(weekClusterFileName, weekRecords);
				put(weekendClusterFileName, weekendRecords);
			}			
		};
		
		for (Entry<String, List<CallRecord>> clustEntry : clustering.entrySet()) {
			HClustCallRecords hClustCallRecords = new HClustCallRecords(clustEntry.getValue(), new MaxLinkageStrategy());
			Set<ClusterInfo> bestClusters = hClustCallRecords.getBestClusters();
			final TreeMap<Double, ClusterStats> clusterStats = computeClusterStats(bestClusters);
			CallStats.printClusterStats(clusterStats, clustEntry.getKey());
//			CallStats.printClusterStats(clusterStats);
		}
				
	}
	
	private Set<ClusterInfo> getBestClusters() throws Exception {
		String[] clusterNames = names();
		distances();
		HierarchyBuilder builder = getHierarchyBuilder(distances, clusterNames, linkageStrategy);
		
		List<Double> costDiffs = new ArrayList<Double>();
		int i = 0;
		Double previous = null;
		
		List<Set<ClusterInfo>> possibleClusters = new ArrayList<Set<ClusterInfo>>();
		
		while (!builder.isTreeComplete()) {
			double cost = cost(builder.getClusters());						
			if (previous != null) {
				double diff = cost - previous;
				costDiffs.add(diff);					
			}
			previous = cost;				
			if (++i >= .8 * clusterNames.length) { // we are interested only in 20% of the clusters in the tail
				possibleClusters.add(customizedClusters(builder.getClusters()));
			}
			builder.agglomerate(linkageStrategy);	
		}
		double mean = Stat.mean(costDiffs);
		double sd = Stat.sd(costDiffs);
		
		for (i = (int) (.8 * clusterNames.length) - 1; i < costDiffs.size(); ++i) {
//			System.out.println(i);
			if (costDiffs.get(i) > mean + 2 * sd) return possibleClusters.get(possibleClusters.size() - (costDiffs.size() - i));				
		}
		return possibleClusters.get(possibleClusters.size() - 1); // return root cluster (all elements are in one cluster)
	}
	
	private void distances() {
		distances = new double[callRecords.size()][callRecords.size()];
		maxDistance = Integer.MIN_VALUE;
		for (int i = 0; i < callRecords.size(); ++i) {
			for (int j = 0; j < callRecords.size(); ++j) {
				int distance = CalendarUtils.distanceInTimeOfDay(callRecords.get(i).startTime, callRecords.get(j).startTime);
				if (distance > maxDistance) maxDistance = distance;
				distances[i][j] = distance;
			}
		}
		// normalize the values to an interval between 0 and 1, assuming the min distance is 0 (the distance of an item from itself)
		if (maxDistance > 0) {
			for (int i = 0; i < callRecords.size(); ++i) {
				for (int j = 0; j < callRecords.size(); ++j) {
					distances[i][j] = distances[i][j] / maxDistance;
				}
			}
		}
	}
	
	private String[] names() {
		String names[] = new String[callRecords.size()];
		for (int i = 0; i < callRecords.size(); ++i) {
			names[i] = callRecords.get(i).toString();
		}
		return names;
	}
	
	private Set<ClusterInfo> customizedClusters(List<Cluster> clusters) throws Exception {
		Set<ClusterInfo> customClusters = new HashSet<ClusterInfo>();
//		int i = 0;
		for (Cluster cluster : clusters) {					
			Deque<Cluster> subClusters = new ArrayDeque<Cluster>();
			subClusters.add(cluster);
			
			ClusterInfo clusterInfo = new ClusterInfo();
			clusterInfo.maxDist = cluster.getDistance() != null ? Math.round(cluster.getDistance() * maxDistance) : 0;
			
//			System.out.println("Max dist=" + clusterInfo.maxDist + ", other dist=" + cluster.getDistance());
			while (!subClusters.isEmpty()) {
				Cluster subCluster = subClusters.pop();
				if (!subCluster.isLeaf()) {
					subClusters.addAll(subCluster.getChildren());
				} else {
//					++i;
					clusterInfo.entries.put(CallRecord.getTimestamp(subCluster.getName()), CallRecord.getPhoneNo(subCluster.getName()));
				}
			}
			customClusters.add(clusterInfo);
		}
		//System.out.println(i);
		return customClusters;
	}
	
	private static TreeMap<Double, ClusterStats> computeClusterStats(Set<ClusterInfo> clusters) {
		TreeMap<Double, ClusterStats> clusterStats = new TreeMap<Double, ClusterStats>();
		
		for (ClusterInfo clusterInfo : clusters) {
			ClusterStats cs = new ClusterStats();
			Map<String, Integer> totalNoOfCallsAfterThisPhoneNo = new HashMap<String, Integer>();
			List<Double> callTimesInSecondsOfTheDay = new ArrayList<Double>();
			//System.out.println("Cluster size: " + clusterInfo.entries.size() + " calls.");
			for (Entry<Calendar, String> calendarPhoneNo : clusterInfo.entries.entrySet()) {
				double secs = (double) CalendarUtils.secInDay(calendarPhoneNo.getKey());
				callTimesInSecondsOfTheDay.add(secs);
				//System.out.print(calendarPhoneNo.getKey().getTime() + "\t" + calendarPhoneNo.getValue());
				if (calendarPhoneNo.getValue().startsWith("O")) { //outgoing call, we are interested
					List<String> states = new ArrayList<String>();					
					Entry<Calendar, String> prev = clusterInfo.entries.lowerEntry(calendarPhoneNo.getKey());
					String key1stOrder = (prev != null && CalendarUtils.distBetweenCalendarsInSeconds(calendarPhoneNo.getKey(), prev.getKey()) <= clusterInfo.maxDist ) ? prev.getValue() : "_";
					states.add(key1stOrder);
					//System.out.print("\t" + key1stOrder); 
					if (prev != null) {
						Entry<Calendar, String> prevPrev = clusterInfo.entries.lowerEntry(prev.getKey());
						if (prevPrev != null && CalendarUtils.distBetweenCalendarsInSeconds(calendarPhoneNo.getKey(), prevPrev.getKey()) <= clusterInfo.maxDist) {
							String key2ndOrder = prevPrev.getValue().concat("_").concat(key1stOrder);
							states.add(key2ndOrder);
							//System.out.print("\t" + key2ndOrder);
						}						
					}
					//System.out.println();
					for (String state : states) {
						TreeMap<String, FreqProb> callStats = cs.markovProb.get(state);
						if (callStats == null) {
							callStats = new TreeMap<String, FreqProb>();
							cs.markovProb.put(state, callStats);
							totalNoOfCallsAfterThisPhoneNo.put(state, 0);
						}
						FreqProb freqProb = callStats.get(calendarPhoneNo.getValue());
						if (freqProb == null) {
							freqProb = new FreqProb(0,0.0);
							callStats.put(calendarPhoneNo.getValue(), freqProb);
						}
						freqProb.freq++;				
						Integer totalFreq = totalNoOfCallsAfterThisPhoneNo.get(state);
						totalNoOfCallsAfterThisPhoneNo.put(state, ++totalFreq);
					}
				} else {
					//System.out.println();
				}
			}
			for (Entry<String, TreeMap<String, FreqProb>> statsEntry : cs.markovProb.entrySet()) {
//				System.out.println(statsEntry.getKey());
				for (Entry<String, FreqProb> callProbEntry : statsEntry.getValue().entrySet()) {
					callProbEntry.getValue().prob = ((double)callProbEntry.getValue().freq) / totalNoOfCallsAfterThisPhoneNo.get(statsEntry.getKey());
//					System.out.println("\t" + callProbEntry.getKey() + "  " + Format.df3.format(callProbEntry.getValue().prob));
				}
			}
			cs.max = Collections.max(callTimesInSecondsOfTheDay);
			//System.out.println();
			for (int i = 0; i < callTimesInSecondsOfTheDay.size(); ++i) {
				if (Math.abs(callTimesInSecondsOfTheDay.get(i) - cs.max) > clusterInfo.maxDist) 
					callTimesInSecondsOfTheDay.set(i, callTimesInSecondsOfTheDay.get(i) + CalendarUtils.secondsInADay);
			}
			cs.min = Collections.min(callTimesInSecondsOfTheDay);
			cs.max = Collections.max(callTimesInSecondsOfTheDay);
//			cs.median  = cs.min + (cs.max - cs.min) / 2;
			cs.mean = Stat.mean(callTimesInSecondsOfTheDay);
			cs.sd = Stat.sd(callTimesInSecondsOfTheDay);
			cs.maxDist = clusterInfo.maxDist;
			clusterStats.put(cs.min, cs);
			//System.out.println();
		}

		return clusterStats;
	}		
	
	private double cost(List<Cluster> clusters) {
		double cost = .0;
		for (Cluster cluster : clusters) {
			cost += sse(cluster);
		}
//		return .5 * clusters.size() * Math.log(callRecords.size()) - cost;
		return cost;
	}
	
	private double sse(Cluster cluster) {
		List<Double> values = new ArrayList<Double>();
		values.add(.0);
		Deque<Cluster> clusters = new ArrayDeque<Cluster>();
		clusters.add(cluster);
		while (!clusters.isEmpty()) {
			Cluster _cluster = clusters.pop();
			if (!_cluster.isLeaf()) {
				values.add(_cluster.getDistance());
				clusters.addAll(_cluster.getChildren());
			}
		}
		
		return Stat.sse(values);
	}
	
	private List<CallRecord> callRecords;
	
	private LinkageStrategy linkageStrategy; 
	
	private double[][] distances;
	
	private double maxDistance;
}
