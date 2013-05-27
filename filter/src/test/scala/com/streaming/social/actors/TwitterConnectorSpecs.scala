package com.streaming.social.actors

import org.specs2.mutable.Specification
import akka.actor.{ActorSystem, Props}
import com.streaming.social.registry._
import com.streaming.social.mq.ProducerAdaptor
import java.net.URLEncoder

class TwitterConnectorSpecs extends Specification {


  "start streaming with one filter" should {
    "get the streaming content from Twitter" in {

      val dummyProducer = new ProducerAdaptor[String] {
        def send(message: String) {
          println(message)
        }
      }

      val system = ActorSystem("MySystem")
      val orchestrator = system.actorOf(Props(new Master(oauth, dummyProducer, url)), name = "master")
      val encodedFilterBy = URLEncoder.encode("real madrid", "UTF-8")
      println(encodedFilterBy)
      orchestrator ! AddFilter(Map("track" -> "real madrid"))
      Thread.sleep(10000)
      orchestrator ! RemoveFilter(Map("track" -> "real madrid"))
    }
  }

  "start streaming with two filter" should {
    "get the streaming content from Twitter" in {

      var filterToCheck = "real madrid"
      val dummyProducer = new ProducerAdaptor[String] {
        def send(message: String) {
          println(message)
        }
      }

      val system = ActorSystem("MySystem")
      val orchestrator = system.actorOf(Props(new Master(oauth, dummyProducer, url)), name = "master")
      val encodedFilterBy = URLEncoder.encode("real madrid", "UTF-8")
      println(encodedFilterBy)
      orchestrator ! AddFilter(Map("track" -> "real madrid"))
      Thread.sleep(10000)
      orchestrator ! AddFilter(Map("track" -> "mourinho"))
      filterToCheck = "mourinho"
      Thread.sleep(10000)
      orchestrator ! RemoveFilter(Map("track" -> "mourinho"))
      filterToCheck = "real madrid"
      Thread.sleep(10000)
      orchestrator ! RemoveFilter(Map("track" -> "real madrid"))
    }
  }

}