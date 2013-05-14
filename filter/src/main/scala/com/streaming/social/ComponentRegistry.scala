package com.streaming.social

import common.{Logging, OAuthProvider}
import mq.MQProducer
import com.typesafe.config.ConfigFactory


object ComponentRegistry {
  def apply() = new ComponentRegistry
}

class ComponentRegistry extends Logging {

  val conf = ConfigFactory.load("application.conf")
  val url = conf.getString("configuration.oauth.http.url")
  val oauth = OAuthProvider(
    method = conf.getString("configuration.oauth.http.method"),
    urlToRequest = conf.getString("configuration.oauth.http.url"),
    consumerKey = conf.getString("configuration.oauth.consumer.key"),
    consumerSecret = conf.getString("configuration.oauth.consumer.secret"),
    oauthToken = conf.getString("configuration.oauth.token.key"),
    oauthTokenSecrete = conf.getString("configuration.oauth.token.secret"))

  val producerStrategy = MQProducer(broker = conf.getString("configuration.mq.broker"),
                                    queue = conf.getString("configuration.mq.queue"))

}