package com.streaming.social.actors

import org.specs2.mutable.Specification
import akka.actor.{ActorSystem, Props}
import com.streaming.social.registry._

class TwitterConnectorSpecs extends Specification {


  "call streamByCriteria" should {
    "get the streaming content from Twitter" in {

      val system = ActorSystem("MySystem")
      val orchestrator = system.actorOf(Props(new Master(oauth, producerStrategy, url)), name = "master")
      orchestrator ! StartSream(Map("track" -> "obama"))
      Thread.sleep(5000)
      orchestrator ! StopStream

    }
  }

}