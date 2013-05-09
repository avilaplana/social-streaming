package com.streaming.social.rest

import unfiltered.request.{Seg, POST, Path}
import akka.actor.{Props, ActorSystem}
import com.streaming.social.actors.{StopStream, StartSream, Master}
import unfiltered.response.Ok
import com.streaming.social.common.{Logging, OAuthProvider}
import com.streaming.social.registry
import com.streaming.social.mq.ProducerAdaptor

class MasterResource extends MasterResourceInt


class MasterResourceInt(oauth: OAuthProvider = registry.oauth, producerStrategy : ProducerAdaptor[String] = registry.producerStrategy, url: String = registry.url) extends unfiltered.filter.Plan with Logging {

  val system = ActorSystem("MySystem")
  val master = system.actorOf(Props(new Master(oauth, producerStrategy, url)), name = "master")

  def intent = {
    case req@POST(Path(Seg("filter" :: "start" :: track :: Nil))) => {
      info(s"Received request to start stream with track: $track")
      master ! StartSream(Map("track" -> track))
      Ok
    }

    case req@POST(Path(Seg("filter" :: "stop" :: Nil))) => {
      info("Received request to stop stream")
      master ! StopStream
      Ok
    }

  }
}