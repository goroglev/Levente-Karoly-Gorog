@Görög Levente-Károly, 27 March 2013

This is a command line twitter client Maven/Eclipse project interfacing with the Twitter streaming API 
(http://dev.twitter.com/pages/streaming_api).

Requirements:
1. Use non-blocking I/O (either direct or through a library)
2. Use statuses/filter method with a configurable list of keywords (i.e. IM)
3. Use json return type
4. Output incoming status information on the command line
5. Application should use spring to configure
6. Project should use a maven2 build file
7. Reconnect logic is a nice to have

Please note that the Twitter4J unofficial Java library for the Twitter API
most likely meets all those requirements except for 5. and 6. However I found it
more important for the sake of the assignment to provide my own code supported
by library functions than to rely entirely on an off-the-shelf 3rd party library.

1. Use non-blocking I/O (either direct or through a library).
I used the HttpComponents HttpAsyncClient (http://hc.apache.org/httpcomponents-asyncclient-dev/)
for non-blocking HTTP messaging along with my own response consumer
(com.client.twitter.StatusesFilterResponseConsumer) implementation.

2. Use statuses/filter method with a configurable list of keywords (i.e. IM)
This refers to the https://stream.twitter.com/1.1/statuses/filter.json endpoint.
Requests sent to this service should be authorized conform the OAuth authorization scheme.
I wrote a wrapper on the top of the oauth-signpost (http://code.google.com/p/oauth-signpost/)
library to facilitate OAuth authorization. The parameters of the OAuth authorization, as well
as that of the statuses/filter method are configured in the spring config file (springApplicationContext.xml).
Furthermore, I use spring beans to facilitate the implementation, invocation and management
of these functionalities (OAuth authorization and statuses/filter method).
NB: it is essential to change the 'consumerKey' and 'consumerSecret' constructor arguments
of the oAuthManager bean in the spring config file prior to running the application.
In the current setup these arguments are set to null which cause spring fail
on creating the OAuth authorization bean. Same goes for the 'accessToken' and 'secretToken'
arguments, although their absence will not cause the application fail but
provide the user with an authorization website URL where they can grant permission 
to the the application to interface with twitter on their behalf.

3. Use json return type
The statuses/filter method returns by definition json content. However
the non-blocking HTTP messaging provides the client with a byte buffer instead of a json string.
I use some straightforward logic to retrieve the individual twitter statuses
from the byte buffer in a byte array. Afterwards I convert the byte array to
string and parse it into json objects using the json-simple library (http://code.google.com/p/json-simple/).
A limited StatusListener implementation attempts to identify different types of twitter statuses 
(tweets, stall warning, track limitations and the rest) by checking simple conditions on the json objects.

4. Output incoming status information on the command line
Tweets: <User name> @<User screen name>: <text of the tweet>
Stall warnings: <the message of the warning>
Track Limitations: The number of undelivered Tweets since the connection was opened: <numberOfLimitedStatuses>
Statuses: the json string  

5. Application should use spring to configure
Application uses the spring-core and spring-context libraries from the springframework (3.2.2.RELEASE).
See 2. and consult the spring config file (springApplicationContext.xml) for more details.

6. Project should use a maven2 build file
See pom.xml in the project's root directory. In order to clean, compile, generate javadoc and create a 
standalone, runnable jar file, use the following goals: clean compile javadoc:javadoc assembly:single

7. Reconnect logic is a nice to have
Not implemented. A //TODO marks the appropriate place in the code com.client.twitter.Main line 37,
along with twitter resources wrt. reconnecting and best practices. 

Some of the classes are documented with javadoc. Unit tests have not been implemented due to lack of time.
  
