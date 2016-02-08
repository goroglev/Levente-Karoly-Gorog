package com.util.oauth;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.util.oauth.OAuthManager;
/**
 * This class represents an oAuth-signed HTTP POST with the 
 * POST parameters added to its entity.
 */
public class OAuthHttpPost extends HttpPost {

	/**
	 * Constructs an oAuth-signed HTTP POST with the 
	 * POST parameters added to its entity.
	 * 
	 * @param uri
	 * @param postParameters
	 * @param oAuthManager
	 * @throws Exception
	 */
	public OAuthHttpPost(String uri, Map<String, String> postParameters, OAuthManager oAuthManager) throws Exception {
		super(uri);
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		for (Entry<String, String> postParam : postParameters.entrySet()) {
			postParams.add(new BasicNameValuePair(postParam.getKey(), postParam.getValue()));
		}
		this.setEntity(new UrlEncodedFormEntity(postParams));
		oAuthManager.sign(this);
	}
}