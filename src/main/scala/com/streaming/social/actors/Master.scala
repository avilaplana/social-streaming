package com.streaming.social.actors

import akka.actor.{ActorSystem, Props, Actor}
import com.streaming.social.common.{Logging, OAuthProvider}


class Master(oauth: OAuthProvider, url: String) extends Actor with Logging{

  val master = ActorSystem("master")

  val connector = master.actorOf(Props(
    new TwitterConnector(url,
      oauth,
      self)),
    name = "connector")

  val extractor = master.actorOf(Props(new TwitterExtractor), name = "extractor")

  val producer = master.actorOf(Props(new Producer), name = "producer")


  def receive = {
    case StartSream(track) => connector ! StartSream(track)
    case Tweet(tweet) => extractor ! Tweet(tweet)
    case TwitterEvent(tweet) => producer ! TwitterEvent(tweet)
    case StopStream => connector ! StopStream
    case _ => error("Message Unknown")
  }
}


