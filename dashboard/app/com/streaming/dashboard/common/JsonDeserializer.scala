package com.streaming.dashboard.common

import com.streaming.dashboard.actor._
import play.libs.Json
import java.text.SimpleDateFormat
import collection.mutable.ListBuffer
import com.streaming.dashboard.actor.TwitterEvent
import com.streaming.dashboard.actor.User
import com.streaming.dashboard.actor.Place
import scala.Some
import com.streaming.dashboard.actor.Recommendations


class JsonDeserializer() {

  val dateFormatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy")

  def extractJsonToObject(jsonString: String): TwitterEvent = {
    val jsonNode = Json.parse(jsonString)
    val text = jsonNode.get("text").asText()
    val created_at = jsonNode.get("created_at").asText()
    val url = jsonNode.get("user").get("profile_image_url").asText()
    val lang = jsonNode.get("lang")
    val language = if (lang == null) None else Some(lang.asText())
    val screenName = jsonNode.get("user").get("screen_name").asText()
    val followers = jsonNode.get("user").get("followers_count").asInt()
    val friends = jsonNode.get("user").get("friends_count").asInt()
    val name = jsonNode.get("user").get("name").asText()
    val gen = jsonNode.get("user").get("gender")
    val gender =  if ( gen == null) None else Some(gen.asText())
    var place: Place = null
    if (jsonNode.get("place") != null) place = Place(full_name = Option(jsonNode.get("place").get("full_name").asText()),
      country_code = Option(jsonNode.get("place").get("country_code").asText()))

    TwitterEvent(text = text, created_at = dateFormatter.parse(created_at),
      lang = language,
      user = User(name = name, screen_name = screenName, profile_image_url = url, followers_count = followers, friends_count = friends, gender = gender),
      place = Option(place))
  }

  def extractJsonToRecommendations(jsonString: String): Recommendations = {
    val jsonNode = Json.parse(jsonString)
    val elements = jsonNode.get("recommendations").getElements
    val recommendations = ListBuffer.empty[Recommendation]
    while (elements.hasNext) {
      val element = elements.next()
      val lang = element.get("language").asText()
      val candidatesJson = element.get("candidates").getElements
      val candidates = ListBuffer.empty[String]
      while (candidatesJson.hasNext) candidates += candidatesJson.next().asText()
      recommendations += Recommendation(language = lang, candidates = candidates.toList)
    }
    Recommendations(recommendations.toList)
  }
}