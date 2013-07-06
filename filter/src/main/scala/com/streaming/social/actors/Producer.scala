package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.{GenderCalculator, JsonSerializer, Logging}
import com.streaming.social.mq.ProducerAdaptor


class Producer(genderCalculator: GenderCalculator, producerStrategy: ProducerAdaptor[String]) extends Actor with Logging {

  val serializer = new JsonSerializer[TwitterEvent]()

  def receive = {
    case tweet: TwitterEvent => {
      val genderTemp = genderCalculator.calculateGender(tweet.user.name)
      val tweetWithGeder = tweet.copy(user = tweet.user.copy(gender = genderTemp))
      producerStrategy.send(serializer.extractObjectToJson(tweetWithGeder))
    }
    case _ => error("Unkown messaege")
  }
}