package com.streaming.social.common

import org.specs2.mutable.Specification
import com.streaming.social.actors.TwitterEvent
import java.util.{TimeZone, Calendar, Date}

class JsonSerializerSpecs extends Specification {

  val json = new JsonDeserializer[TwitterEvent]()

  "desirialized json" should {
    "create a instance of TwitterEvent" in {
      val twitterInstance :TwitterEvent = json.extractJsonToObject(twitterJson)
      twitterInstance.text must_== "This is a exampl of json for the test"
      twitterInstance.user.profile_image_url must_== "http://a0.twimg.com/profile_images/2995540720/0dfc4bc235fa893150b6865a33b33ed4_normal.jpeg"
      twitterInstance.created_at must_!= null
      twitterInstance.user.screen_name must_== "willstauff"
      twitterInstance.lang must_==  "en"
    }
  }

  val twitterJson =
    """
      {"created_at":"Sun Apr 21 11:18:34 +0000 2013",
      "id":325931562988347392,
      "id_str":"325931562988347392",
      "text":"This is a exampl of json for the test",
      "source":"\u003ca href=\"http:\/\/patriotjournalist.com\" rel=\"nofollow\"\u003ePatriotJournalist\u003c\/a\u003e",
      "truncated":false,
      "in_reply_to_status_id":null,
      "in_reply_to_status_id_str":null,
      "in_reply_to_user_id":null,
      "in_reply_to_user_id_str":null,
      "in_reply_to_screen_name":null,
      "user":{
        "id":21775637,
        "id_str":"21775637",
        "name":"William Stauff",
        "screen_name":"willstauff",
        "location":" GA",
        "url":"http:\/\/www.facebook.com\/willstauff",
        "description":"Name is Will Stauff, Conservative, Christian, Family Man, Wife Marilyn, Four year old daughter Emma #TCOT #TGDN #CCOT #PJNET #LNYHBT #GAGOP",
        "protected":false,
        "followers_count":10077,
        "friends_count":11065,
        "listed_count":152,
        "created_at":"Tue Feb 24 17:47:38 +0000 2009",
        "favourites_count":84,
        "utc_offset":-18000,
        "time_zone":"Eastern Time (US & Canada)",
        "geo_enabled":false,
        "verified":false,
        "statuses_count":42507,
        "lang":"en",
        "contributors_enabled":false,
        "is_translator":false,
        "profile_background_color":"C0DEED",
        "profile_background_image_url":"http:\\/\/a0.twimg.com\/profile_background_images\/834274217\/9c38c969718df0afada448a5db7f5956.jpeg",
        "profile_background_image_url_https":"https:\/\/si0.twimg.com\/profile_background_images\/834274217\/9c38c969718df0afada448a5db7f5956.jpeg",
        "profile_background_tile":true,
        "profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/2995540720\/0dfc4bc235fa893150b6865a33b33ed4_normal.jpeg",
        "profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/2995540720\/0dfc4bc235fa893150b6865a33b33ed4_normal.jpeg",
        "profile_banner_url":"https:\/\/si0.twimg.com\/profile_banners\/21775637\/1363107971",
        "profile_link_color":"0084B4",
        "profile_sidebar_border_color":"FFFFFF",
        "profile_sidebar_fill_color":"DDEEF6",
        "profile_text_color":"333333",
        "profile_use_background_image":true,
        "default_profile":false,
        "default_profile_image":false,
        "following":null,
        "follow_request_sent":null,
        "notifications":null
        },
        "geo":null,
        "coordinates":null,
        "place":null,
        "contributors":null,
        "retweet_count":0,
        "favorite_count":0,
        "entities":{"hashtags":[{"text":"Benghazi","indices":[65,74]}],"symbols":[],"urls":[{"url":"http:\/\/t.co\/m0dS5OYebh",
        "expanded_url":"http:\/\/ow.ly\/kd1hu","display_url":"ow.ly\/kd1hu","indices":[121,143]}],"user_mentions":[]},
        "favorited":false,
        "retweeted":false,
        "possibly_sensitive":false,
        "filter_level":"medium",
        "lang":"en"}

    """


}