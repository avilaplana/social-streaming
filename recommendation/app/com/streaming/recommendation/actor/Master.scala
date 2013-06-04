package com.streaming.dashboard.actor

import akka.actor._
import scala.concurrent.duration._

import play.api.libs.concurrent._


import play.api.Play.current
import com.streaming.recommendation.actor.{TwitterEvent, Message, Consumer}
import com.streaming.recommendation.common.Logging
import akka.event.slf4j.Logger

object Master extends Logging {

  implicit val timeout = akka.util.Timeout(1 second)

  lazy val default = {
    val master = Akka.system.actorOf(Props(new Master()))
    master
  }
}

class Master() extends Actor with Logging {

  val consumerActor = Akka.system.actorOf(Props(new Consumer(self)), "consumer")

  def receive = {
    case StartConsumer => consumerActor ! StartConsumer
    case tweeet: TwitterEvent => debug(s"Master receives a tweet. Ready to be analysed $tweeet" )
    case _ : Object => info(s"message not valid")
  }
}

case class StartConsumer() extends Message
