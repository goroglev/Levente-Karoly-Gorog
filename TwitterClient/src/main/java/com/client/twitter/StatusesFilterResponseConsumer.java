package com.client.twitter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

    /** 
     * Async consumer of twitter's statuses/filter endpoint (https://stream.twitter.com/1.1/statuses/filter.json).
     * For API documentation check out https://dev.twitter.com/docs/api/1.1/post/statuses/filter 
     */
    public class StatusesFilterResponseConsumer extends AbstractAsyncResponseConsumer<StatusLine> {
 
    	private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8 * 1024); // read max. 8 MB from the response body in one go
    	
    	private byte[] twitterStatus = null; // the byte array representation of a twitter status JSON string
    	private int twitterStatusLength = 0; // the length of a twitter status (in bytes)
    	private int twitterStatusPosition = 0; // the number of bytes read/processed from a twitter status
    	private StringBuilder statusLengthSniffer = new StringBuilder(); // "sniff" newlines ("\r\n") followed by the length of the upcoming twitter status
    	private StatusListener statusListener = new LimitedStatusListener(); // detect and handle different types of statuses 
    	
		/**
		 * Parses twitter statuses incrementally from the received content. 
		 * 
		 * @see org.apache.http.nio.protocol.AbstractAsyncResponseConsumer#onContentReceived(org.apache.http.nio.ContentDecoder, org.apache.http.nio.IOControl)
		 */
		@Override
		protected void onContentReceived(ContentDecoder contentDecoder, IOControl ioControl)
				throws IOException {
			// while content is coming from the server
			while (contentDecoder.read(this.byteBuffer) > 0) {				
				// set the buffer for reading
				this.byteBuffer.flip();
				// while the buffer is not empty
				while (this.byteBuffer.hasRemaining()) {					
					if (this.twitterStatusLength == 0) { // no twitter status under processing
						// try to read a newline -  
						// either as a keep-alive message from the server or as a separator between twitter statuses
						while (this.byteBuffer.hasRemaining() && this.twitterStatusLength == 0) {
							// at this point we expect only newlines "\r\n" or digits (of the status length)
							// these are always ASCII characters represented by a single byte
							this.statusLengthSniffer.append((char)this.byteBuffer.get());
							// we are looking for (\r\n)*[1-9][0-9]*\r\n patterns
							int newLineIndex = this.statusLengthSniffer.indexOf("\r\n"); 
							if (newLineIndex != -1) {
								this.statusLengthSniffer.setLength(newLineIndex);
								if (this.statusLengthSniffer.length() > 0) {
									try {
										this.twitterStatusLength = Integer.parseInt(this.statusLengthSniffer.toString());
										this.twitterStatus = new byte[this.twitterStatusLength];
										this.twitterStatusPosition = 0;
										this.statusLengthSniffer.setLength(0);
									} catch (NumberFormatException e) {
										// this is unexpected. Let's clear the sniffer and continue sniffing.
										this.statusLengthSniffer.setLength(0);
									}
								}
							}
						}
					} else {
						int noOfTwitterStatusBytesToBeCopied = this.byteBuffer.remaining() < this.twitterStatusLength - this.twitterStatusPosition ?
								this.byteBuffer.remaining() : this.twitterStatusLength - this.twitterStatusPosition;
						this.byteBuffer.get(this.twitterStatus, this.twitterStatusPosition, noOfTwitterStatusBytesToBeCopied);
						this.twitterStatusPosition += noOfTwitterStatusBytesToBeCopied;
						if (this.twitterStatusPosition == this.twitterStatusLength) { // the twitter status is complete
							this.twitterStatusLength = 0;
							// the encoding for the json content type is UTF-8 
							String jsonString = new String(this.twitterStatus, Charset.forName("UTF-8"));
							// parse the JSON string
							JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
							// evaluate the JSON object
							StatusEvaluator.evaluate(statusListener, jsonObject);							
						}
					}				
				}
				this.byteBuffer.clear();
			}
		}
		
		/**
		 * Returns the response status line.
		 * 
		 * @see org.apache.http.nio.protocol.AbstractAsyncResponseConsumer#buildResult(org.apache.http.protocol.HttpContext)
		 */
		@Override
		protected StatusLine buildResult(HttpContext httpContext) throws Exception {
			HttpResponse response = (HttpResponse) httpContext.getAttribute("http.response");
			return response.getStatusLine();
		}


		/* (non-Javadoc)
		 * @see org.apache.http.nio.protocol.AbstractAsyncResponseConsumer#onEntityEnclosed(org.apache.http.HttpEntity, org.apache.http.entity.ContentType)
		 */
		@Override
		protected void onEntityEnclosed(HttpEntity httpEntity, ContentType contentType)
				throws IOException {
		}

		/* (non-Javadoc)
		 * @see org.apache.http.nio.protocol.AbstractAsyncResponseConsumer#onResponseReceived(org.apache.http.HttpResponse)
		 */
		@Override
		protected void onResponseReceived(HttpResponse httpResponse)
				throws HttpException, IOException {	
		}

		/* (non-Javadoc)
		 * @see org.apache.http.nio.protocol.AbstractAsyncResponseConsumer#releaseResources()
		 */
		@Override
		protected void releaseResources() {			
			
		}
       
    }