package com.streaming.social.common

import org.specs2.mutable.Specification
import com.streaming.social.actors.{User, TwitterEvent}
import java.util.{Calendar, Date}


class JsonDeserializerSpecs extends Specification {

  val serializer = new JsonSerializer[TwitterEvent]()

  "serialize a tweet" should {
    "generate a json" in {
      val cal = Calendar.getInstance()
      cal.set(2013,1,1,0,0,0)

      val json = serializer.extractObjectToJson(TwitterEvent(
        text = "this is a test",
        created_at = cal.getTime,
        lang = "en",
        User(profile_image_url = "http://test.com/images/test.jpg",
             screen_name = "testuser")))

      json must_== jsonExpected
    }
  }

  val jsonExpected = """{"text":"this is a test","created_at":"Fri Feb 1 00:00:00 +0000 2013","lang":"en","user":{"profile_image_url":"http://test.com/images/test.jpg","screen_name":"testuser"}}"""

}