package com.streaming.recommendation.common

import play.libs.Json
import java.text.SimpleDateFormat
import com.streaming.recommendation.actor.{User, TwitterEvent}


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

    TwitterEvent(text = text, created_at = dateFormatter.parse(created_at),
      lang = language,
      user = User(screen_name = name,profile_image_url = url,followers_count = followers, friends_count = friends ))

    }

}