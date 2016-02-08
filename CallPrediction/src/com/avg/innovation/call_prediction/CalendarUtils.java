package com.avg.innovation.call_prediction;

import java.util.Calendar;

public class CalendarUtils {
	
	public static int secondsInADay = 24 * 3600; //approximation
	
	public static int secInDay(Calendar cal) {
		return cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
	}
	
	public static String timeOfDay(int seconds) {
		if (seconds > secondsInADay) seconds = seconds - secondsInADay;
		return seconds / 3600 + ":" + (seconds % 3600) / 60 + ":" + seconds % 60;
	}

	public static boolean isSameDate(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) return false;
		return cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	public static int distanceInTimeOfDay(Calendar cal1,
			Calendar cal2) {		
		if (cal1 == null || cal2 == null) return 0;
		int secOfDay1 = secInDay(cal1);
		int secOfDay2 = secInDay(cal2);
		int distance = Math.abs(secOfDay1 - secOfDay2);
		return Math.min(distance, CalendarUtils.secondsInADay - distance);
	}
	
	public static int distBetweenCalendarsInSeconds(Calendar cal1,
			Calendar cal2) {
		if (cal1 == null || cal2 == null) return 0;
		return Math.abs((int)(cal1.getTimeInMillis() - cal2.getTimeInMillis())/1000);
	}

}
