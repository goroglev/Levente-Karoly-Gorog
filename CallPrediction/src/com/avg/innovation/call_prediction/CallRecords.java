package com.avg.innovation.call_prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CallRecords {
	
	/**
	 * parse a call log, returning a list of subsequent call records 
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public CallRecords(String fileName) throws Exception {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(new File(fileName)));
			boolean isInput = true;
			callRecords = new ArrayList<CallRecord>();
			phoneNumbers = new HashSet<String>();
			HashSet<String> prefixes = new HashSet<String>();
			String line = input.readLine();
			do {
				// see an example for a call record in CallRecord.java
				String[] callRecordArray = new String[4];
				for (int i = 0; i < 4; ++i) {
					line = input.readLine();
					if (line != null) {
						callRecordArray[i] = line;
					} else {
						isInput = false;
						break;
					}				
				}				
				if (isInput) {		
					CallRecord callRecord = new CallRecord(callRecordArray);
					String phoneNo = callRecord.phoneNo;
					HashSet<String> formats = new HashSet<String>();
					if (phoneNo.startsWith("+")) {
						formats.add(phoneNo.replaceFirst("^\\+", "00"));
						formats.add(phoneNo.replaceFirst("^\\+\\d{2}", "0"));
					} else if (phoneNo.startsWith("00")) {
						formats.add(phoneNo.replaceFirst("^00", "+"));
						formats.add(phoneNo.replaceFirst("^00\\d{2}", "0"));
					} else {
						for (String prefix : prefixes) {
							formats.add(phoneNo.replaceFirst("^0", prefix));
						}
					}
					for (String format : formats) {						
						if (phoneNumbers.contains(format)) {
							phoneNo = format;
							break;
						}
					}
					
					if (phoneNo.startsWith("+")) prefixes.add(phoneNo.substring(0, 3));
					if (phoneNo.startsWith("00")) prefixes.add(phoneNo.substring(0, 4));
					
					phoneNumbers.add(phoneNo);					
					callRecord.phoneNo = phoneNo;
					callRecords.add(callRecord);										
				}
				input.readLine();
			} while (isInput);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	public List<CallRecord> daysOfWeek(boolean skipShortCalls, Integer...days) {
		List<CallRecord> daysOfWeek = new ArrayList<CallRecord>();		
		HashSet<Integer> _days = new HashSet<Integer>(Arrays.asList(days));
		for (CallRecord callRecord : callRecords) {
			if (callRecord.duration > CallRecord.SHORT_DURATION) {
				int day = callRecord.startTime.get(Calendar.DAY_OF_WEEK); 
				if (_days.contains(day)) {
					daysOfWeek.add(callRecord);
				}
			}
		}
		return daysOfWeek;
	}
	
	public List<CallRecord> callRecords; 
	
	public Set<String> phoneNumbers;

}
