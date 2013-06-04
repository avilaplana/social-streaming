package com.streaming.recommendation.actor

import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}
import akka.actor.{Actor, ActorRef}
import scala.Predef.String
import java.util.Date
import com.streaming.dashboard.actor.StartConsumer
import com.streaming.recommendation.common.{Logging, JsonDeserializer}


class Consumer(master: ActorRef) extends Actor with Logging {

  def receive = {
    case StartConsumer => info("Start Consmuming tweets");configureConnection
    case ConsumeMessage(consumer) => consumeMessage(consumer)
  }


  def configureConnection = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()

    val channel = connection.createChannel()


    channel.exchangeDeclare("tweets", "fanout");
    val queueName = channel.queueDeclare().getQueue();
    channel.queueBind(queueName, "tweets", "");

    val consumer = new QueueingConsumer(channel);
    channel.basicConsume(queueName, true, consumer);

    self ! ConsumeMessage(consumer)
  }

  def consumeMessage(consumer: QueueingConsumer) {
    val delivery = consumer.nextDelivery();
    val msg = new String(delivery.getBody());
    val event = new JsonDeserializer().extractJsonToObject(msg)
    master ! event
    self ! ConsumeMessage(consumer)
  }
}


trait Message

case class ConsumeMessage(consumer: QueueingConsumer) extends Message

case class TwitterEvent(val text: String, val created_at: Date, val lang: Option[String], val user: User) extends Message

case class User(val profile_image_url: String, val screen_name: String, val followers_count: Integer, val friends_count: Integer)
