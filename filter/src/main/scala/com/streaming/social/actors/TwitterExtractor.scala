package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.Json
import akka.event.Logging


class TwitterExtractor extends Actor{
  val json = new Json[TwitterEvent]()
  def receive = {

    case Tweet(tweet) => sender !  json.extractJsonToObject(tweet)
  }
}