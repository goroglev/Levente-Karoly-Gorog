package com.client.twitter;

import java.util.concurrent.Future;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.util.oauth.OAuthHttpPost;


public class Main {

	public static void main(String[] args) throws Exception {

		// retrieve the spring application context
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("springApplicationContext.xml");
		// get spring create an oAuthHttpPost bean, using its configuration from the application context xml 
		OAuthHttpPost oAuthHttpPost = applicationContext.getBean("oAuthHttpPost", OAuthHttpPost.class);	

		// create an async http client using the org.apache.http.nio.client.* libraries
		// the code below is adopted from the "Event based content streaming" example of HttpAsyncClient,
		// check out at http://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/examples/org/apache/http/examples/nio/client/AsyncClientHttpExchangeStreaming.java
		HttpAsyncClient httpclient = new DefaultHttpAsyncClient();		
        httpclient.start();
        try {
            // initiate HTTP messaging over a non-blocking HTTP connection
        	// see the implementation of StatusesFilterResponseConsumer  
        	Future<StatusLine> future = httpclient.execute(HttpAsyncMethods.create(oAuthHttpPost), new StatusesFilterResponseConsumer(), null);
            StatusLine status = future.get(); 
            if (status == null || status.getStatusCode() != HttpStatus.SC_OK) {
            	System.out.println("Request failed. " + status != null ? status.toString() : "");
            	//TODO reconnect, having in mind:
            	// Reconnecting for twitter: https://dev.twitter.com/docs/streaming-apis/connecting#Reconnecting
            	// Best practices for twitter: https://dev.twitter.com/docs/streaming-apis/connecting#Best_practices
            } else {
            	System.out.println("Request successfully completed.");
            }
            System.out.println("Shutting down...");
        } finally {
            httpclient.shutdown();
        }
        System.out.println("Async HTTP client has disconnected.");
	}

}
