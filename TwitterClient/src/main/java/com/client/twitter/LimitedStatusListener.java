package com.client.twitter;

import org.json.simple.JSONObject;

public class LimitedStatusListener implements StatusListener {
	
	public void onTweet(JSONObject tweet) {
		JSONObject user = (JSONObject) tweet.get("user");
		System.out.println(user.get("name") + " @" + user.get("screen_name") + ": " + tweet.get("text") );
	}
	
	public void onStallWarning(JSONObject warning) {
		JSONObject warn = (JSONObject) warning.get("warning");
		if (warn.containsKey("message")) System.out.println(warn.get("message")); // this should be String
		
	}
	
	public void onTrackLimitation(int numberOfLimitedStatuses) {
		System.out.println("The number of undelivered Tweets since the connection was opened: " + numberOfLimitedStatuses);
	}
	
	public void onStatus(JSONObject status) {		
		// a bit cynical: convert the JSONObject back to String and output the JSON string as is.
		// for demonstration purposes only.
		System.out.println(status.toJSONString());
	}

}
