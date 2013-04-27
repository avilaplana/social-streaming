package com.streaming.social.rest

import unfiltered.request.{Seg, POST, Path}
import akka.actor.{Props, ActorSystem}
import com.streaming.social.actors.{StopStream, StartSream, Master}
import unfiltered.response.Ok
import com.streaming.social.common.OAuthProvider
import com.streaming.social.registry

class MasterResource extends MasterResourceInt


class MasterResourceInt(oauth: OAuthProvider = registry.oauth, url: String = registry.url) extends unfiltered.filter.Plan {

  val system = ActorSystem("MySystem")
  val master = system.actorOf(Props(new Master(oauth, url)), name = "master")

  def intent = {
    case req@POST(Path(Seg("filter" :: "start" :: track :: Nil))) => {
      println("I received the kick off")
      master ! StartSream("obama")
      Ok
    }

    case req@POST(Path(Seg("filter" :: "stop" :: Nil))) => {
      println("I received the stop")
      master ! StopStream
      Ok
    }

  }
}