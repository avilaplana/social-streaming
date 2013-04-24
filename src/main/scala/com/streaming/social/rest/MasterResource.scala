package com.streaming.social.rest

import unfiltered.request.{Seg, POST, Path}
import akka.actor.{Props, ActorSystem}
import com.streaming.social.actors.{Finish, StartSream, Master}
import unfiltered.response.Ok


class MasterResource extends unfiltered.filter.Plan {

  val system = ActorSystem("MySystem")
  val master = system.actorOf(Props(new Master), name = "master")

  def intent = {
    case req@POST(Path(Seg("filter" :: "start" :: track :: Nil))) => {
      println("I received the kick off")
      master ! StartSream("obama")
      Ok
    }

    case req@POST(Path(Seg("filter" :: "stop" :: Nil))) => {
      println("I received the stop")
      master ! Finish
      Ok
    }

  }
}