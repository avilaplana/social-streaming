package com.streaming.social.mq

import com.rabbitmq.client.ConnectionFactory


trait ProducerAdaptor[T] {
  def send(message: T)
}

case class MQProducer() extends ProducerAdaptor[String] {

  val QUEUE_NAME = "hello"
  val factory = new ConnectionFactory();
  factory.setHost("localhost");

  def send(message: String) {
    val connection = factory.newConnection();
    val channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

    channel.close();
    connection.close();
  }
}