package com.streaming.dashboard.actor

import akka.actor._
import scala.concurrent.duration._

import play.api.libs.concurrent._


import play.api.Play.current
import com.streaming.recommendation.actor._
import com.streaming.recommendation.common.Logging
import collection.mutable.ListBuffer
import com.streaming.recommendation.actor.TwitterEvent
import com.streaming.recommendation.mq.MQProducer
import concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object Master extends Logging {

  implicit val timeout = akka.util.Timeout(1 second)

  lazy val default = {
    val master = Akka.system.actorOf(Props(new Master()))
    master
  }
}

class Master() extends Actor with Logging {

  var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
  val consumerActor = Akka.system.actorOf(Props(new Consumer(self)), "consumer")
  val analizerActor = Akka.system.actorOf(Props(new Analizer(self)), "analizer")
  val producerActor = Akka.system.actorOf(Props(new Producer(MQProducer(broker = "localhost",
    queue = "recommendations"))), "producer")

  Akka.system.scheduler.schedule(
    30 seconds,
    30 seconds,
    self,
    Recommendations(RecommendationEngine.calculateRecommendations(candidates, 10)))

  def receive = {
    case StartConsumer => consumerActor ! StartConsumer
    case tweeet: TwitterEvent => analizerActor ! tweeet
    case candidate: RecommendationCandidate => candidates = RecommendationEngine.insertCandidate(candidate.trend, candidate.language, candidates)
    case recommendations: Recommendations => producerActor ! Recommendations(RecommendationEngine.calculateRecommendations(candidates, 10)); candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    case _ => info(s"message not valid")
  }
}

case class StartConsumer() extends Message

case class RecommendationCandidate(trend: String, language: Option[String]) extends Message

case class Recommendations(recommendations: Map[String, List[String]])
