package com.client.twitter;

import org.json.simple.JSONObject;

public class StatusEvaluator {
	
	/**
	 * Determines the 'nature' of a twitter status (stall warning, limit notices, tweets & everything else).  
	 * 
	 * @param statusListener
	 * @param status
	 */
	public static void evaluate(StatusListener statusListener, JSONObject status) {		
		if (status.containsKey("warning")) { // stall warning
			statusListener.onStallWarning(status);
		} else if (status.containsKey("limit")) { // limit notices
			JSONObject limit = (JSONObject) status.get("limit");
			if (limit.containsKey("track"))
			statusListener.onTrackLimitation(Integer.parseInt(limit.get("track").toString()));
		} else if (status.containsKey("created_at") && status.containsKey("user") && status.containsKey("entities")) { // tweets
			statusListener.onTweet(status);
		} else statusListener.onStatus(status); // everything else: Status deletion, Location deletion, Withheld content etc. notices
	}

}
