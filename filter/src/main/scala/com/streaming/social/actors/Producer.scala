package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.{JsonSerializer, Logging}
import com.streaming.social.mq.ProducerAdaptor


class Producer(producerStrategy : ProducerAdaptor[String]) extends Actor with Logging{

  val serializer = new JsonSerializer[TwitterEvent]()

  def receive = {
    case tweet : TwitterEvent => producerStrategy.send(serializer.extractObjectToJson(tweet))
    case _ => error("Unkown messaege")
  }

}