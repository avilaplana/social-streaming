package com.streaming.dashboard.actor

import com.rabbitmq.client.{ConnectionFactory, QueueingConsumer}
import akka.actor.{ActorRef, Actor}
import scala.Predef._
import java.util.Date
import com.streaming.dashboard.common.{Logging, JsonDeserializer}


class Consumer(master: ActorRef) extends Actor with Logging {

  var languageToFilterBy: Option[String] = None

  def receive = {
    case StartConsumer => configureConnection
    case ConsumeMessage(consumer) => consumeMessage(consumer)
    case FilterByLanguage(language) => languageToFilterBy = language
  }


  def configureConnection = {

    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    val connection = factory.newConnection()

    val channel = connection.createChannel()
    channel.queueDeclare("tweet.queue", true, false, false, null)
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume("tweet.queue", true, consumer)
    self ! ConsumeMessage(consumer)
  }

  // todo the consumer does not know anything about filtering. It is not responsible
  def consumeMessage(consumer: QueueingConsumer) {
    val delivery = consumer.nextDelivery();
    val msg = new String(delivery.getBody());
    val event = new JsonDeserializer().extractJsonToObject(msg)
    if ((languageToFilterBy.isDefined && event.lang.isDefined && event.lang == languageToFilterBy)
      || (languageToFilterBy.isEmpty)) master ! event
    self ! ConsumeMessage(consumer)
  }
}


trait Message

case class ConsumeMessage(consumer: QueueingConsumer) extends Message

case class TwitterEvent(val text: String, val created_at: Date, val lang: Option[String], val user: User) extends Message

case class User(val profile_image_url: String, val screen_name: String)
