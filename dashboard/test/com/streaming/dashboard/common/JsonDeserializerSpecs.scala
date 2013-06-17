package com.streaming.dashboard.common

import com.streaming.dashboard.common.JsonDeserializer
import org.specs2.mutable.Specification


class JsonDeserializerSpecs extends Specification {

  val deserializer = new JsonDeserializer
  val jsonRecommendations = """ {"recommendations":[{"language":"es","candidates":["testes1","testes2"]},{"language":"it","candidates":["testit1","testit2","testit3"]}]}"""
  val jsonWithoutLanguage = """{"text":"this is a test","created_at":"Fri Feb 1 00:00:00 +0000 2013","user":{"profile_image_url":"http://test.com/images/test.jpg","screen_name":"testuser","followers_count":99,"friends_count":199},"place":{"full_name":"Murcia, Spain","country_code":"ES"}}"""
  val jsonWithLanguage = """{"text":"this is a test","created_at":"Fri Feb 1 00:00:00 +0000 2013","lang":"en","user":{"profile_image_url":"http://test.com/images/test.jpg","screen_name":"testuser","followers_count":99,"friends_count":199},"place":{"full_name":"Murcia, Spain","country_code":"ES"}}"""

  //todo add more checks
  "deserialize json without language" should {
    "create an instance of TwitterEvent" in {
      val twitterEvent = deserializer.extractJsonToObject(jsonWithoutLanguage)
      twitterEvent.text must_== "this is a test"
      twitterEvent.user.screen_name must_== "testuser"
      twitterEvent.user.profile_image_url must_== "http://test.com/images/test.jpg"
      twitterEvent.lang.isEmpty must beTrue
    }
  }

  //todo add more checks
  "deserialize json without language" should {
    "create an instance of TwitterEvent" in {
      val twitterEvent = deserializer.extractJsonToObject(jsonWithLanguage)
      twitterEvent.text must_== "this is a test"
      twitterEvent.user.screen_name must_== "testuser"
      twitterEvent.user.profile_image_url must_== "http://test.com/images/test.jpg"
      twitterEvent.lang.get must_== "en"
    }
  }



  "deserialize json recommendations" >> {
      val recommendations = deserializer.extractJsonToRecommendations(jsonRecommendations)
      recommendations.listRecommendationds.size must_== 2
      recommendations.listRecommendationds(0).language must_== "es"
      recommendations.listRecommendationds(0).candidates must_== List("testes1","testes2")
      recommendations.listRecommendationds(1).language must_== "it"
      recommendations.listRecommendationds(1).candidates must_== List("testit1","testit2","testit3")
  }


}