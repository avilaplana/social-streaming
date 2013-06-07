package com.streaming.recommendation.actor

import akka.actor.Actor
import com.streaming.recommendation.mq.ProducerAdaptor
import com.streaming.recommendation.common.{JsonSerializer, Logging}
import com.streaming.dashboard.actor.Recommendations


class Producer(producerStrategy: ProducerAdaptor[String]) extends Actor with Logging {

  def receive = {
    case recommendations: Recommendations => producerStrategy.send(JsonSerializer.serializer(recommendations.recommendations))
    case _ => error("Unkown messaege")
  }

}