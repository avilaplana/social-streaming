package com.streaming.dashboard.conf

import play.api.{Application, GlobalSettings}
import akka.actor.Props
import play.libs.Akka
import com.streaming.dashboard.actor.{StartConsumer, Consumer}


object Global extends GlobalSettings {
  override def onStart(app: Application) {
    super.onStart(app)
    println("running the consumer")
    val consumer = Akka.system.actorOf(Props(new Consumer()), "consumer")
    consumer ! StartConsumer
    println("Consumer started")
  }

  override def onStop(app: Application) {
    super.onStop(app)
  }
}