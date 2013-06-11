package com.streaming.dashboard.common

import com.streaming.dashboard.actor.{Place, TwitterEvent, User}
import play.libs.Json
import java.text.SimpleDateFormat


class JsonDeserializer() {

  val dateFormatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy")

  def extractJsonToObject(jsonString: String): TwitterEvent = {
    val jsonNode = Json.parse(jsonString)
    val text = jsonNode.get("text").asText()
    val created_at = jsonNode.get("created_at").asText()
    val url = jsonNode.get("user").get("profile_image_url").asText()
    val language = if (jsonNode.get("lang") == null) None else Some(jsonNode.get("lang").asText())
    val name = jsonNode.get("user").get("screen_name").asText()
    val followers = jsonNode.get("user").get("followers_count").asInt()
    val friends = jsonNode.get("user").get("friends_count").asInt()
    var place: Place = null
    if (jsonNode.get("place") != null) place = Place(full_name = Option(jsonNode.get("place").get("full_name").asText()),
      country_code = Option(jsonNode.get("place").get("country_code").asText()))


    TwitterEvent(text = text, created_at = dateFormatter.parse(created_at),
      lang = language,
      user = User(screen_name = name, profile_image_url = url, followers_count = followers, friends_count = friends),
      place = Option(place))

  }

}