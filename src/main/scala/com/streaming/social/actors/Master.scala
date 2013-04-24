package com.streaming.social.actors

import akka.actor.{Kill, ActorSystem, Props, Actor}
import akka.event.Logging
import com.streaming.social.common.OAuthProvider


class Master extends Actor {

  val log = Logging(context.system, this)

  val master = ActorSystem("master")

  val connector = master.actorOf(Props(
    new TwitterConnector("https://stream.twitter.com/1/statuses/filter.json",
      new OAuthProvider())),
    name = "connector")

  val extractor =  master.actorOf(Props(new TwitterExtractor),name = "extractor")

  val producer =  master.actorOf(Props(new Producer),name = "producer")


  def receive = {
    case StartSream(track) => connector !  StartSream(track)
    case Tweet(tweet) => extractor ! Tweet(tweet)
    case TwitterEvent(tweet) => producer  ! TwitterEvent(tweet)
    case Finish => log.info("To kill it"); connector ! Finish
    case _ => log.error("Message Unknown")
  }
}



