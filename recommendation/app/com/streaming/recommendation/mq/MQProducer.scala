package com.streaming.recommendation.mq

import com.rabbitmq.client.{MessageProperties, ConnectionFactory}


trait ProducerAdaptor[T] {
  def send(message: T)
}

case class MQProducer(broker: String, queue: String) extends ProducerAdaptor[String] {


  val factory = new ConnectionFactory();
  factory.setHost(broker);

  def send(message: String) {
    val connection = factory.newConnection();
    val channel = connection.createChannel();
    channel.queueDeclare(queue, true, false, false, null);
    channel.basicPublish("", queue, MessageProperties.TEXT_PLAIN, message.getBytes());

    channel.close();
    connection.close();
  }
}