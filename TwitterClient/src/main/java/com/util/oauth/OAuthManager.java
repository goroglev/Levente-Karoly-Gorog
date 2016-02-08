package com.util.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpRequest;

/**
 * Implementation of the OAuth authorization scheme\n
 * Reference documentation: http://tools.ietf.org/html/rfc5849\n 
 * Terms: 'the API' refers to a custom web site API which requires OAuth authorization\n
 */
public class OAuthManager {
	

    private final OAuthConsumer consumer;

    private final OAuthProvider provider;
    
    private static final String[] EMPTY = new String[] {};

    /**
     * Constructs http components (http://hc.apache.org/)-compatible oAuth consumer and provider  
     * for a given API and gets the user grant authorization to this app (to use the API on their behalf) 
     * if the consumer's access- and secret token have not been provided.
     * 
     * @param consumerKey
     * @param consumerSecret
     * @param requestTokenEndpointUrl 
     * @param accessTokenEndpointUrl
     * @param authorizationWebsiteUrl
     * @param accessToken
     * @param secretToken
     *
     * @throws Exception 
     */
    public OAuthManager(final String consumerKey, final String consumerSecret, final String requestTokenEndpointUrl,
            final String accessTokenEndpointUrl, final String authorizationWebsiteUrl, final String accessToken,
            final String secretToken) throws Exception {

    	// construct a default OAuth consumer
    	this.consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        // construct a default OAuth provider
    	this.provider = new CommonsHttpOAuthProvider(requestTokenEndpointUrl, accessTokenEndpointUrl,
                authorizationWebsiteUrl);
        
    	// if accessToken or secretToken is null, authorize the user    	
    	if (accessToken == null || secretToken == null) authorize();
    	else this.consumer.setTokenWithSecret(accessToken, secretToken);	
    }
    
    /**
     * Signs the HTTP request according the OAuth scheme and the API prior to sending it to the server.
     * 
     * @param request - the HTTP request to the API which needs to be signed
     * 
     * @throws OAuthMessageSignerException
     * @throws OAuthExpectationFailedException
     * @throws OAuthCommunicationException 
     */
    public void sign(final HttpRequest request) throws OAuthMessageSignerException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        this.consumer.sign(request);
    }
    
    private void authorize() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
		String authUrl = this.provider.retrieveRequestToken(this.consumer, OAuth.OUT_OF_BAND, EMPTY);		
        System.out.println("Visit: " + authUrl);
        System.out.println("... and grant this app authorization");
        System.out.println("Enter the PIN code and hit ENTER when you're done:");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String pin = br.readLine();

        System.out.println("Fetching access token...");

        provider.retrieveAccessToken(this.consumer, pin, EMPTY);

        System.out.println("Access token: " + consumer.getToken());
        System.out.println("Token secret: " + consumer.getTokenSecret());
    }   
}
