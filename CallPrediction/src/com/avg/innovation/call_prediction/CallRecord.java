package com.avg.innovation.call_prediction;

import java.text.ParseException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * represents a call record from an exported android call log
 *	E.g. Phone Number:--- +31618664810 
 * 	Call Type:--- OUTGOING 
 * 	Call Date:--- Mon May 12 19:23:10 CEST 2014 
 * 	Call duration in sec :--- 60
 * 
 * 
 * 
 * @author adminuser
 *
 */
public class CallRecord {
	
	public CallRecord(String[] callRecord) throws Exception {
		String[] fields = new String[4];
		for (int i = 0; i < 4; ++i) {
			Matcher match = callRecordPatterns[i].matcher(callRecord[i]);
			if (match.find()) {
				fields[i] = match.group(1);
			} else throw new Exception("Expected `" + callRecordPatterns[i].pattern() + "`, got `" + callRecord[i] + "`");
		}
		CallType callType;
		try {
			callType = CallType.valueOf(fields[1]);
		} catch (IllegalArgumentException e) {
			callType = null;
		}
		
		this.phoneNo = fields[0];
		this.type = callType;
		this.startTime = Calendar.getInstance();
		this.startTime.setTime(Format.dateFormat.parse(fields[2]));
		this.duration = Integer.parseInt(fields[3]);

			
	}
	
	public CallRecord(String phoneNo, CallType callType, Calendar startTime, int duration) {
		this.phoneNo = phoneNo;
		this.type = callType;
		this.startTime = startTime;
		this.duration = duration;
	}
	
	public static Calendar getTimestamp(String callRecordName) throws ParseException {
		String[] splits = callRecordName.split("_");
		Calendar cal = Calendar.getInstance();
		cal.setTime(Format.dateFormat.parse(splits[0]));
		return cal;
	}
	
	public static String getPhoneNo(String callRecordName) throws ParseException {
		String[] splits = callRecordName.split("_");
		return splits[1];
	}
	
	public String toString() {
		return startTime.getTime() + "_" + type.toString().charAt(0) + phoneNo;		
//		return "  " + startTime.get(Calendar.HOUR_OF_DAY) + ":" + startTime.get(Calendar.MINUTE) + "  ";
	}	

	public String phoneNo;
	
	public CallType type;
	
	public Calendar startTime;
	
	public int duration; // seconds

	public static int SHORT_DURATION = 7;
	
	private static Pattern[] callRecordPatterns = {
		Pattern.compile("Phone Number:--- ((\\*#)*\\+?-?\\d+(\\*#)*)"),
		Pattern.compile("Call Type:--- ([A-z]+)"),
		Pattern.compile("Call Date:--- (.+\\d)"),
		Pattern.compile("Call duration in sec :--- (\\d+)")
	};


}
