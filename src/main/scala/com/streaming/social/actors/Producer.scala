package com.streaming.social.actors

import akka.actor.Actor
import com.streaming.social.common.Logging


class Producer extends Actor with Logging{
  def receive = {
    case tweet : TwitterEvent => info(s"Receiving the tweet : $tweet")
    case _ => error("Unkown messaege")
  }

}