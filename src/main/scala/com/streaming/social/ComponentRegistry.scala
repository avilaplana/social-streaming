package com.streaming.social

import common.OAuthProvider
import mq.MQProducer


object ComponentRegistry {
  def apply() = new ComponentRegistry
}

class ComponentRegistry {
  val url = "https://stream.twitter.com/1/statuses/filter.json"
  val oauth = OAuthProvider(
    method = "POST",
    urlToRequest = url,
    consumerKey = "AAAAAA",
    consumerSecret = "AAAAA",
    oauthToken = "AAAAAA",
    oauthTokenSecrete = "AAAAAA")
  val producerStrategy = MQProducer()

}