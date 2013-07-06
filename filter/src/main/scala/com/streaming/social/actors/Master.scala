package com.streaming.social.actors

import akka.actor.{ActorSystem, Props, Actor}
import com.streaming.social.common.{GenderCalculator, Logging, OAuthProvider}
import com.streaming.social.mq.ProducerAdaptor


class Master(oauth: OAuthProvider, producerStrategy : ProducerAdaptor[String], url: String, genderCalculator: GenderCalculator) extends Actor with Logging{

  val master = ActorSystem("master")

  val connector = master.actorOf(Props(
    new TwitterConnector(url,
      oauth,
      self)),
    name = "connector")

  val extractor = master.actorOf(Props(new TwitterExtractor), name = "extractor")
  val gender = master.actorOf(Props(new TwitterExtractor), name = "gender")
  val producer = master.actorOf(Props(new Producer(genderCalculator, producerStrategy)), name = "producer")

  def receive = {
    case AddFilter(parameters) => connector ! AddFilter(parameters)
    case Tweet(tweet) => extractor ! Tweet(tweet)
    case TwitterEvent(tweet, created_at, lang, user, place) => producer ! TwitterEvent(tweet, created_at, lang, user, place)
    case RemoveFilter(parameters) => connector ! RemoveFilter(parameters)
    case _ => error("Message Unknown")
  }
}



