package com.streaming.recommendation.actor

import collection.mutable.ListBuffer
import com.streaming.recommendation.common.Logging


object RecommendationEngine extends Logging {

  def insertCandidate(candidate: String, language: Option[String], candidates: Map[String, collection.mutable.HashMap[Int, ListBuffer[String]]]) = {
    var candidatesTemp = candidates
//    debug(s"Recommendation detected -> $candidate")
    language match {
      case Some(lang) => candidates.get(lang) match {
        case None => {
          candidatesTemp = candidatesTemp + (lang -> collection.mutable.HashMap(1 -> ListBuffer(candidate)))
        }
        case Some(map) => {
          var entry = map.filter(element => element._2.contains(candidate))
          if (entry.isEmpty) {
            map.get(1) match {
              case None => map += (1 -> ListBuffer(candidate))
              case Some(list) => list += candidate
            }
          } else if (entry.size == 1) {
            map + (entry.head._1 -> (entry.head._2 -= candidate))
            map.get(entry.head._1 + 1) match {
              case None => {
                val newCounter = entry.head._1 + 1
                map += (newCounter -> ListBuffer(candidate))
              }
              case Some(list) => list += candidate
            }
          } else new RuntimeException(s"trend ${candidate} has different counters")
        }
      }
      case None =>
    }
//    debug(s"Recommendation candidates $candidatesTemp")
    candidatesTemp
  }


  def calculateRecommendations(candidates: Map[String, collection.mutable.HashMap[Int, ListBuffer[String]]], numberRecommendationPerLang : Int) = {
    var languagesRecommendation = Map.empty[String, List[String]]
    candidates.foreach {
      languageEntry =>
        var mapCountersWithNoEmptyValues = languageEntry._2.filter(counterEntry => !counterEntry._2.isEmpty)
        var countersSorted = mapCountersWithNoEmptyValues.keys.toList.sortWith((a, b) => a > b)
        var recommendationsPerLanguageSorted = countersSorted.map(counter => mapCountersWithNoEmptyValues.get(counter).get).flatten
        languagesRecommendation = languagesRecommendation + (languageEntry._1 -> recommendationsPerLanguageSorted.take(numberRecommendationPerLang))
    }
    info(s"Recommendations calculated: $languagesRecommendation")
    languagesRecommendation
  }
}