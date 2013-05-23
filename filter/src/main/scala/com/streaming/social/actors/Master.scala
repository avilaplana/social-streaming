package com.streaming.social.actors

import akka.actor.{ActorSystem, Props, Actor}
import com.streaming.social.common.{Logging, OAuthProvider}
import com.streaming.social.mq.ProducerAdaptor


class Master(oauth: OAuthProvider, producerStrategy : ProducerAdaptor[String], url: String) extends Actor with Logging{

  val master = ActorSystem("master")

  val connector = master.actorOf(Props(
    new TwitterConnector(url,
      oauth,
      self)),
    name = "connector")

  val extractor = master.actorOf(Props(new TwitterExtractor), name = "extractor")

  val producer = master.actorOf(Props(new Producer(producerStrategy)), name = "producer")


  def receive = {
    case StartSream(parameters) => connector ! StartSream(parameters)
    case Tweet(tweet) => extractor ! Tweet(tweet)
    case TwitterEvent(tweet, created_at, lang, user) => producer ! TwitterEvent(tweet, created_at, lang, user)
    case StopStream => connector ! StopStream
    case _ => error("Message Unknown")
  }
}



