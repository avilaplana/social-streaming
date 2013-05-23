package com.streaming.social.actors

import org.specs2.mutable.Specification
import akka.actor.{ActorSystem, Props}
import com.streaming.social.registry._
import com.streaming.social.mq.ProducerAdaptor
import java.net.URLEncoder

class TwitterConnectorSpecs extends Specification {

  val dummyProducer = new ProducerAdaptor[String] {
    def send(message: String) {
      println(s"Message to be sent is $message")
    }
  }
  "call streamByCriteria" should {
    "get the streaming content from Twitter" in {

      val system = ActorSystem("MySystem")
      val orchestrator = system.actorOf(Props(new Master(oauth, dummyProducer, url)), name = "master")
      val encodedFilterBy = URLEncoder.encode("real madrid", "UTF-8")
      println(encodedFilterBy)
      orchestrator ! StartSream(Map("track" -> "real madrid"))
      Thread.sleep(40000)
      orchestrator ! StopStream

    }
  }

}