package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.Json
import akka.event.Logging


class TwitterExtractor extends Actor{
  val log = Logging(context.system, this)
  val json = new Json[TwitterEvent]()
  def receive = {

    case Tweet(tweet) => log.debug("aaaaaa"); sender !  json.extractJsonToObject(tweet)
  }
}