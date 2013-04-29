package com.streaming.social.common

import net.liftweb.json.{DefaultFormats, JsonParser}
import java.text.SimpleDateFormat

class Json[T](implicit man: Manifest[T]) {

  implicit val formats = new DefaultFormats {
     override def dateFormatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy")
   }

  def extractJsonToObject(jsonString: String): T = {
    val json = JsonParser.parse(jsonString)
    json.extract[T]
  }
}