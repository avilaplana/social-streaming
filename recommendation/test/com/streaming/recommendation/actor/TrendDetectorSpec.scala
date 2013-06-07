package com.streaming.recommendation.actor

import org.specs2.mutable.Specification


class TrendDetectorSpec extends Specification{

  "Applying the regular expression" should {
    "give the trends for the tweet" in {
      val candidates  = TrendDetector.detect("This is # in #London and #Paris but the best Olympic games where in #Barcelona92")
      candidates.size must_== 3
      candidates.contains("#London") must beTrue
      candidates.contains("#Paris") must beTrue
      candidates.contains("#Barcelona92") must beTrue
    }
  }

}