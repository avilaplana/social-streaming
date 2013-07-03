package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.JsonDeserializer
import akka.event.Logging


class TwitterExtractor extends Actor {
  val json = new JsonDeserializer[TwitterEvent]()

  def receive = {

    case Tweet(tweet) => {
      val tweetEvent = json.extractJsonToObject(tweet)
      tweetEvent.copy(user = tweetEvent.user.copy(gender = Some("male")))
      sender ! tweetEvent
    }
  }
}