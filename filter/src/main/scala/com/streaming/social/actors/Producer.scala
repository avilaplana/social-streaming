package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.Logging
import com.streaming.social.mq.ProducerAdaptor


class Producer(producerStrategy : ProducerAdaptor[String]) extends Actor with Logging{
  def receive = {
    case tweet : TwitterEvent => producerStrategy.send(tweet.text)
    case _ => error("Unkown messaege")
  }

}