package com.avg.innovation.call_prediction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class PredictNextCall {
	
	
	
	/**
	 * suggests up to NR_PHONE_NUMBERS_TO_SUGGEST phone numbers likely to call next based on one's call history
	 * @param callRecords
	 * @param weekClusterFileName
	 * @param weekendClusterFileName
	 * @return
	 * @throws IOException 
	 */
	
	public static Collection<String> predict(final String callLogFile, final String weekClusterFileName, final String weekendClusterFileName) throws Exception {
		List<CallRecord> callRecords = new CallRecords(callLogFile).callRecords;
		Collection<String> suggestions = new LinkedHashSet<String>();
		if (callRecords.size() == 0) return suggestions;
		
		
		Calendar now = Calendar.getInstance();
		Calendar oneDayAgo = Calendar.getInstance();
		oneDayAgo.setTimeInMillis(now.getTimeInMillis() - CalendarUtils.secondsInADay * 1000);
		
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
		CallStats callStats = isWeekend ? new CallStats(weekendClusterFileName) : new CallStats(weekClusterFileName);

		int secIn2day = CalendarUtils.secInDay(now);
		TreeMap<Double, ClusterStats> clusterProbs = callStats.getClusterProbabilities(secIn2day);
		ClusterStats mostLikelyClust = clusterProbs.lastEntry().getValue();
		
		CallRecord lastCall = callRecords.get(0);
		// if last call is unsuccessful (missed, rejected, not picked up, or very short) then it's likely we'd like to call that number (if the call is no older than a day)
		if (lastCall.duration <= CallRecord.SHORT_DURATION && lastCall.startTime.after(oneDayAgo)) suggestions.add(lastCall.phoneNo);
		
		// get the last 2 successful calls
		List<CallRecord> last2SuccessCalls = new ArrayList<CallRecord>();
		for (CallRecord callRecord : callRecords) {
			if (CalendarUtils.distBetweenCalendarsInSeconds(callRecord.startTime, now) > mostLikelyClust.maxDist) break;
			ClusterStats cs = callStats.getClusterProbabilities(CalendarUtils.secInDay(callRecord.startTime)).lastEntry().getValue();
			if (cs != mostLikelyClust) break;
			if (callRecord.duration > CallRecord.SHORT_DURATION) {
				last2SuccessCalls.add(callRecord);
			}
			if (last2SuccessCalls.size() == 2) break; // got the last 2 success calls
		}
		
		LinkedList<String> states = new LinkedList<String>();
		states.add("_"); // empty states
		String state = "";
		for (CallRecord callRecord : last2SuccessCalls) {
			if (state.equals("")) state = callRecord.type.toString().charAt(0) + callRecord.phoneNo;
			else state = callRecord.type.toString().charAt(0) + callRecord.phoneNo + "_" + state;
			states.add(state);
		}
		Collections.reverse(states);
		
		NavigableMap<String, Double> candidates = new TreeMap<String, Double>();
		for (int i = 0; i < states.size(); ++i) {
			for (Entry<Double, ClusterStats> clustProb : clusterProbs.descendingMap().entrySet()) {
				TreeMap<String, FreqProb> subCandidates = clustProb.getValue().markovProb.get(states.get(i));
				if (subCandidates != null) {
					for (Entry<String, FreqProb> markovProb : subCandidates.entrySet()) {
						if (markovProb.getValue().freq >= 2) {
							Double candidateProb = candidates.get(markovProb.getKey());
							if (candidateProb == null) candidateProb = .0;
							candidateProb += clustProb.getKey() * markovProb.getValue().prob * (1/(Math.pow(states.size(), i) + 1));
							candidates.put(markovProb.getKey(), candidateProb);
						}
					}
				}
			}
		}
		
		int i = 0;
		
		for (Entry<String, Double> candidate : Sorter.sortByValue(candidates).descendingMap().entrySet()) {
			if (++i <= PredictNextCall.NR_PHONE_NUMBERS_TO_SUGGEST) suggestions.add(candidate.getKey());
			else break;
//			System.out.println(" " + candidate.getKey().substring(1) + "\t" + candidate.getValue());
		}
//		System.out.println();
		return suggestions;
	}
	
	public static void main(String[] args) throws Exception {		
		predict(args[0], args[1], args[2]);	
	}
	
	private static final int NR_PHONE_NUMBERS_TO_SUGGEST = 5;

}
