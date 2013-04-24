package com.streaming.social.actors

import akka.actor.Actor
import akka.event.Logging


class Producer extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case tweet : TwitterEvent => log.info("Receiving the tweet : %s".format(tweet))
    case _ => log.error("Unkown messaege")
  }

}