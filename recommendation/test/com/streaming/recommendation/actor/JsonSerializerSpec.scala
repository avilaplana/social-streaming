package com.streaming.recommendation.actor

import org.specs2.mutable.Specification
import com.streaming.recommendation.common.JsonSerializer


class JsonSerializerSpec extends Specification{

  val jsonToCheck = """{"recommendations":[{"language":"en","candidates":["test1","test2","test3"]},{"language":"es","candidates":["test1","test2","test3"]}]}"""

  "Recommendation to serialize in Json format" >> {
    val recommendationsInJsonFormat = JsonSerializer.serializer(Map("en" -> List("test1", "test2", "test3"),
                                          "es" -> List("test1", "test2", "test3")))
    recommendationsInJsonFormat must_== jsonToCheck

  }

}