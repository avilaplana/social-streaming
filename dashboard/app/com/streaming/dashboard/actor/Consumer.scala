package com.streaming.dashboard.actor

import com.rabbitmq.client.{ConnectionFactory, QueueingConsumer}
import akka.actor.{ActorRef, Actor}
import scala.Predef._
import java.util.Date
import com.streaming.dashboard.common.JsonDeserializer


class Consumer(master: ActorRef) extends Actor {

  def receive = {
    case StartConsumer => startReceving
  }


  def startReceving = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()

    val channel = connection.createChannel()
    channel.queueDeclare("tweet.queue", true, false, false, null)
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume("tweet.queue", true, consumer)

    while (true) {
      val delivery = consumer.nextDelivery();
      val msg = new String(delivery.getBody());
      master ! new JsonDeserializer().extractJsonToObject(msg)
    }
  }
}


trait Message

case class StartConsumer() extends Message
case class TwitterEvent(val text: String, val created_at: Date, val user: User) extends Message
case class User(val profile_image_url: String, val screen_name: String)