package com.streaming.recommendation.actor

import akka.actor.{ActorRef, Actor}
import com.streaming.recommendation.common.Logging
import com.streaming.dashboard.actor.RecommendationCandidate


class Analizer(master: ActorRef) extends Actor with Logging {
  def receive = {
    case tweet: TwitterEvent => {
      val trends = TrendDetector.detect(tweet.text)
      trends.foreach(trend => master ! RecommendationCandidate(trend, tweet.lang))
    }
  }
}

object TrendDetector {

  def detect(stringToAnalize: String) = {
    "#([^\\s]+)".r.findAllMatchIn(stringToAnalize).map(element => element.matched).toSet
  }
}
