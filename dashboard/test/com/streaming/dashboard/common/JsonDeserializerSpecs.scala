package com.streaming.dashboard.common

import com.streaming.dashboard.common.JsonDeserializer
import org.specs2.mutable.Specification



class JsonDeserializerSpecs extends Specification{

  val deserializer = new JsonDeserializer
  "deserialize json without language" should {
    "create an instance of TwitterEvent" in {
      val twitterEvent = deserializer.extractJsonToObject(jsonWithoutLanguage)
      twitterEvent.text must_== "this is a test"
      twitterEvent.user.screen_name must_== "testuser"
      twitterEvent.user.profile_image_url must_== "http://test.com/images/test.jpg"
      twitterEvent.lang.isEmpty must beTrue
    }
  }

  "deserialize json without language" should {
      "create an instance of TwitterEvent" in {
        val twitterEvent = deserializer.extractJsonToObject(jsonWithLanguage)
        twitterEvent.text must_== "this is a test"
        twitterEvent.user.screen_name must_== "testuser"
        twitterEvent.user.profile_image_url must_== "http://test.com/images/test.jpg"
        twitterEvent.lang.get must_== "es"
      }
    }

  val jsonWithoutLanguage = """{"text":"this is a test","created_at":"Fri Feb 1 00:00:00 +0000 2013","user":{"profile_image_url":"http://test.com/images/test.jpg","screen_name":"testuser"}}"""
  val jsonWithLanguage = """{"text":"this is a test","created_at":"Fri Feb 1 00:00:00 +0000 2013","lang":"es","user":{"profile_image_url":"http://test.com/images/test.jpg","screen_name":"testuser"}}"""


}