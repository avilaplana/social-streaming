package com.streaming.recommendation.actor

import org.specs2.mutable.Specification
import collection.mutable.ListBuffer


class RecommendationEngineSpec extends Specification {

  val firstCandidate = "#ThisTestFirst"
  val secondCandidate = "#ThisTestSecond"
  val thirdCandidate = "#ThisTestThird"

  "Adding two different candidates for the same language" >> {
    var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates.size must_== 1
    candidates.get("en").isDefined must beTrue
    candidates.get("en").get.size must_== 1
    candidates.get("en").get.get(1).isDefined must beTrue
    candidates.get("en").get.get(1).get must_== ListBuffer(firstCandidate)

    candidates = RecommendationEngine.insertCandidate(secondCandidate, Some("en"), candidates)
    candidates.size must_== 1
    candidates.get("en").isDefined must beTrue
    candidates.get("en").get.size must_== 1
    candidates.get("en").get.get(1).isDefined must beTrue
    candidates.get("en").get.get(1).get must_== ListBuffer(firstCandidate, secondCandidate)

  }

  "Adding two candidates for the different language" >> {

    var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates.size must_== 1
    candidates.get("en").isDefined must beTrue
    candidates.get("en").get.size must_== 1
    candidates.get("en").get.get(1).isDefined must beTrue
    candidates.get("en").get.get(1).get must_== ListBuffer(firstCandidate)


    candidates = RecommendationEngine.insertCandidate(secondCandidate, Some("es"), candidates)
    candidates.size must_== 2
    candidates.get("en").isDefined must beTrue
    candidates.get("en").get.size must_== 1
    candidates.get("en").get.get(1).isDefined must beTrue
    candidates.get("en").get.get(1).get must_== ListBuffer(firstCandidate)
    candidates.get("es").isDefined must beTrue
    candidates.get("es").get.size must_== 1
    candidates.get("es").get.get(1).isDefined must beTrue
    candidates.get("es").get.get(1).get must_== ListBuffer(secondCandidate)


  }

  "Adding twice the same candidate for the same language" >> {
    var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)

    candidates.size must_== 1
    candidates.get("en").isDefined must beTrue
    candidates.get("en").get.size must_== 2
    candidates.get("en").get.get(1).isDefined must beTrue
    candidates.get("en").get.get(1).get.isEmpty must beTrue
    candidates.get("en").get.get(2).isDefined must beTrue
    candidates.get("en").get.get(2).get must_== ListBuffer(firstCandidate)
  }

  "Adding twice the same candidate and another candidate once for the same language" >> {
    var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(secondCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)

    candidates.size must_== 1
    candidates.get("en").isDefined must beTrue
    candidates.get("en").get.size must_== 2
    candidates.get("en").get.get(1).isDefined must beTrue
    candidates.get("en").get.get(1).get must_== ListBuffer(secondCandidate)
    candidates.get("en").get.get(2).isDefined must beTrue
    candidates.get("en").get.get(2).get must_== ListBuffer(firstCandidate)
  }

  "Adding candidate for no langage" >> {
    val firstCandidate = "#ThisTestFirst"
    var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    candidates = RecommendationEngine.insertCandidate(firstCandidate, None, candidates)
    candidates.isEmpty must beTrue
  }


  "calculate recommendations" >> {
    var candidates = Map.empty[String, collection.mutable.HashMap[Int, ListBuffer[String]]]
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("en"), candidates)

    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("es"), candidates)
    candidates = RecommendationEngine.insertCandidate(firstCandidate, Some("es"), candidates)

    candidates = RecommendationEngine.insertCandidate(secondCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(secondCandidate, Some("en"), candidates)

    candidates = RecommendationEngine.insertCandidate(thirdCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(thirdCandidate, Some("en"), candidates)
    candidates = RecommendationEngine.insertCandidate(thirdCandidate, Some("en"), candidates)

    candidates = RecommendationEngine.insertCandidate(thirdCandidate, Some("es"), candidates)

    val recommendationsPerLanguage3 = RecommendationEngine.calculateRecommendations(candidates, 5)
    recommendationsPerLanguage3.isEmpty must beFalse
    recommendationsPerLanguage3.get("en").isDefined must beTrue
    recommendationsPerLanguage3.get("es").isDefined must beTrue
    recommendationsPerLanguage3.get("en").get.size must_== 3
    recommendationsPerLanguage3.get("es").get.size must_== 2
    recommendationsPerLanguage3.get("en").get must_== List(firstCandidate, thirdCandidate, secondCandidate)
    recommendationsPerLanguage3.get("es").get must_== List(firstCandidate, thirdCandidate)

    val recommendationsPerLanguage2 = RecommendationEngine.calculateRecommendations(candidates, 2)
    recommendationsPerLanguage2.isEmpty must beFalse
    recommendationsPerLanguage2.get("en").isDefined must beTrue
    recommendationsPerLanguage2.get("es").isDefined must beTrue
    recommendationsPerLanguage2.get("en").get.size must_== 2
    recommendationsPerLanguage2.get("es").get.size must_== 2
    recommendationsPerLanguage2.get("en").get must_== List(firstCandidate, thirdCandidate)
    recommendationsPerLanguage2.get("es").get must_== List(firstCandidate, thirdCandidate)

    val recommendationsPerLanguage1 = RecommendationEngine.calculateRecommendations(candidates, 1)
    recommendationsPerLanguage1.isEmpty must beFalse
    recommendationsPerLanguage1.get("en").isDefined must beTrue
    recommendationsPerLanguage1.get("es").isDefined must beTrue
    recommendationsPerLanguage1.get("en").get.size must_== 1
    recommendationsPerLanguage1.get("es").get.size must_== 1
    recommendationsPerLanguage1.get("en").get must_== List(firstCandidate)
    recommendationsPerLanguage1.get("es").get must_== List(firstCandidate)


  }
}
