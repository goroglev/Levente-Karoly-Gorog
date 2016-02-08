package com.client.twitter;

import org.json.simple.JSONObject;

public interface StatusListener {
	
	void 	onTweet(JSONObject tweet); 
	
	void	onStallWarning(JSONObject warning);	
	
	void	onTrackLimitation(int numberOfLimitedStatuses);		
	
	void	onStatus(JSONObject status);

}
