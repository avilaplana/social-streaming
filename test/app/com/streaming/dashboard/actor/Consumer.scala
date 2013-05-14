package com.streaming.dashboard.actor

import akka.actor.Actor
import com.rabbitmq.client.{ConnectionFactory, QueueingConsumer}


class Consumer extends Actor {
  def receive = {
    case StartConsumer => startReceving
  }


  def startReceving = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()

    val channel = connection.createChannel()
    channel.queueDeclare("tweet.queue", true, false, false, null);
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume("tweet.queue", true, consumer);

    while (true) {
      val delivery = consumer.nextDelivery();
      val msg = new String(delivery.getBody());
      println(s"The message received is $msg")
    }
  }
}