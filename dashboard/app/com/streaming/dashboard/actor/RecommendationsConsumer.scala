package com.streaming.dashboard.actor

import akka.actor.{Actor, ActorRef}
import com.streaming.dashboard.common.{JsonDeserializer, Logging}
import com.rabbitmq.client.{QueueingConsumer, ConnectionFactory}

class RecommendationsConsumer(master: ActorRef) extends Actor with Logging {

  var languageToFilterBy: Option[String] = None

  def receive = {
    case StartConsumer => configureConnection
    case ConsumeMessage(consumer) => consumeMessage(consumer)
  }


  def configureConnection = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()

    val channel = connection.createChannel()
    channel.queueDeclare("recommendations", true, false, false, null)
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume("recommendations", true, consumer)
    self ! ConsumeMessage(consumer)
  }

  // todo the consumer does not know anything about filtering. It is not responsible
  def consumeMessage(consumer: QueueingConsumer) {
    val delivery = consumer.nextDelivery();
    val msg = new String(delivery.getBody());
    val event = new JsonDeserializer().extractJsonToObject(msg)
    master ! event
    self ! ConsumeMessage(consumer)
  }
}
