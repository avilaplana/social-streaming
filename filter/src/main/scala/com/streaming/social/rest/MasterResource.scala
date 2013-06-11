package com.streaming.social.rest

import unfiltered.request.{Seg, POST, Path}
import akka.actor.{Props, ActorSystem}
import com.streaming.social.actors.{RemoveFilter, AddFilter, Master}
import unfiltered.response.Ok
import com.streaming.social.common.{Logging, OAuthProvider}
import com.streaming.social.registry
import com.streaming.social.mq.ProducerAdaptor
import java.net.URLDecoder

class MasterResource extends MasterResourceInt


class MasterResourceInt(oauth: OAuthProvider = registry.oauth,
                        producerStrategy: ProducerAdaptor[String] = registry.producerStrategy,
                        url: String = registry.url,
                        coordinates: Map[String,String] = registry.coordinates) extends unfiltered.filter.Plan with Logging {

  val system = ActorSystem("MySystem")
  val master = system.actorOf(Props(new Master(oauth, producerStrategy, url)), name = "master")

  def intent = {
    case req@POST(Path(Seg("filter" :: "add" :: track :: Nil))) => {
      val trackDecoded = URLDecoder.decode(track.asInstanceOf[String], "UTF-8")
      info(s"Received request to add filter: $trackDecoded")
      master ! AddFilter(Map("track" -> trackDecoded))
      Ok
    }

    case req@POST(Path(Seg("location" :: "add" :: country :: Nil))) => {
      info(s"Received request to add location: $country")
      master ! AddFilter(Map("locations" -> coordinates.get(country.asInstanceOf[String].toUpperCase).getOrElse(throw new RuntimeException)))
      Ok
    }

    case req@POST(Path(Seg("filter" :: "remove" :: track :: Nil))) => {
      val trackDecoded = URLDecoder.decode(track.asInstanceOf[String], "UTF-8")
      info(s"Received request to remove filter: $trackDecoded")
      master ! RemoveFilter(Map("track" -> trackDecoded))
      Ok
    }

    case req@POST(Path(Seg("location" :: "remove" :: country :: Nil))) => {
      info(s"Received request to remove location: $country")
      master ! RemoveFilter(Map("locations" -> coordinates.get(country.asInstanceOf[String].toUpperCase).getOrElse(throw new RuntimeException)))
      Ok
    }

  }
}