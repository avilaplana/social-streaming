package com.streaming.social.common

import org.specs2.mutable.Specification
import com.streaming.social.actors.{Place, User, TwitterEvent}
import java.util.{Calendar, Date}


class JsonSerializerSpecs extends Specification {

  val serializer = new JsonSerializer[TwitterEvent]()

  "serialize a tweet" should {
    "generate a json" in {
      val cal = Calendar.getInstance()
      cal.set(2013,1,1,0,0,0)

      val json = serializer.extractObjectToJson(TwitterEvent(
        text = "this is a test",
        created_at = cal.getTime,
        lang = Some("en"),
        User(name = "testName", profile_image_url = "http://test.com/images/test.jpg",
             screen_name = "testuser",
            followers_count = 99,
        friends_count = 199), Some(Place(full_name = Some("Murcia, Spain"), country_code = Some("ES")))))

      json must_== jsonExpected
    }
  }

  val jsonExpected = """{"text":"this is a test","created_at":"Fri Feb 1 00:00:00 +0000 2013","lang":"en","user":{"name":"testName","profile_image_url":"http://test.com/images/test.jpg","screen_name":"testuser","followers_count":99,"friends_count":199},"place":{"full_name":"Murcia, Spain","country_code":"ES"}}"""

}