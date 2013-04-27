This project is an exercise to use interesting technologies and newest frameworks.
Any suggestion or collaboration is always welcome.

This project builds a platform with the following components

1. Web component with two end points -> under development

		1.1 /filter/start/{track} -> gets the stream from Twitter in real time filtered by that track and push in a external message system
		1.2 /filter/stop -> stops the stream from Twitter
		
		Technologies used:
			1. Scala 2.10
			2. Akka Actor 2.1.0
			3. Unfiltered 0.6.8
			4. Dispatch Oauth 0.8.9
			5. Apache Http Client 4.2.3
			6. Servlet 2.5
			
2. Message system to queue tweets filtered by previous component -> Not developed and open to pick (AMQ, RabbitMQ, Kafka.....)

3. Web component with the following -> Not developed and open to pick (Play 2.0, HTML5)
	
		3.1 Start stream with a track
		3.2 Stop stream 
		3.3 Visualize tweets in real time for that track
		




