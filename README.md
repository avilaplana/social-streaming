**Introduction**

The idea behind this project is to learn more (or everything) about Scala and all the ecosystem around. The reason why I share this here is because I would like people from the community to give me feedbacks and new features to develop.

**The initial idea**

The purpose of this exercise is to create a platform that provides real-time streaming from different social networks, i.e. Twitter 
and Facebook. There is a user interface called 'Dashboard' that permits to the user introduce 'Search & Filters parameters' to
make more useful the content. 

Behind the scenes, there is another application called 'Filter' that will connect with the social networks to get the stream for a set of filters. That content
will  be sent to a broker. The 'Dashboard' will consume the stream from the broker and will show it to the users.

In terms of technologies, I bet for Scala ecosystem, because the functional languages are becoming more and more popular
in the community, and I bevieve that is very important to have the functional point of view of the things instead of beaing object oriented all the time.


The next diagram shows the first initial development for the project

![Initial development](https://raw.github.com/avilaplana/social-streaming/master/documentation/initial.png)

**The Architecture**

The platform has 3 different components:

1. Filter web app
2. Dashboard web app 
3. RabbitMQ broker

***

**Architectural limitations of this approach**
TODO

***

**Filter web app**

This web app has 3 functionalities:

1. **Rest API** to add/remove filters
2. Connector to **Twitter Streaming Endpoint**
3. AMQP Producer stream to broker **RabbitMQ**

Technologies

1. Scala 2.10
2. Akka Actor 2.1.0
3. Unfiltered 0.6.8
4. Dispatch Oauth 0.8.9
5. Apache Http Client 4.2.3
6. AMQP client 3.0.4
7. Lift Json 2.5
8. Specs2 1.13
9. SBT 0.12.3

The role of Akka framework
TODO 

How to run it?

Go to the filter folder and execute 

1. `./sbt`
2. Once you are in the sbt console, execute `container:start`

**Note:** Run before the broker
***

**RabbitMQ**

Download the last version of [RabbitMQ](http://www.rabbitmq.com/). The version that I use is 3.1.0

How to run it?

1. Run the broker executing `$RABBITMQ_HOME/sbin/rabbitmq-server`
2. Open the browser and go to `http://localhost:15672`. This is the management tool where you can control events coming in and coming out. 

**Dashboard web app**

This web app has 3 functionalities:

1. **User interface**
2. **Rest Client** to add/remove filters to Filter web app
3. AMQP Consumer stream from broker **RabbitMQ**

Technologies

1. Scala 2.10
2. Play 2.1.1
3. Akka Actor 2.1.0
4. Bootstrap 1.4
5. AMQP client 3.0.4
6. SBT 0.12.3

The role of Akka framework
TODO
How to run it?

Go to the dashboard folder and execute 

1. `$PLAY_HOME/play` 
2. Once you are in the play console, execute `run`
3. Open the browser and go to `localhost:9000`

The next snapshot shows the user interface (to be improved.... a lot)

![Social Networks talk](https://raw.github.com/avilaplana/social-streaming/master/documentation/dashboard.png)

***

**Next Steps**

1. Adding Facebook stream

The next diagram show the architecture

![Adding Facebook](https://raw.github.com/avilaplana/social-streaming/master/documentation/diagram.png)

2. Deploy in a real environment

![Deploy real environment](https://raw.github.com/avilaplana/social-streaming/master/documentation/third.png)

3. Add newest frameworks if they are useful

4. Add proper testing for frameworks like Akka and Play. I mean create good quality integration and acceptance tests 


