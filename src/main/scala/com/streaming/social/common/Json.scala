package com.streaming.social.common

import net.liftweb.json.JsonParser

class Json[T](implicit man: Manifest[T]) {

  implicit val formats = net.liftweb.json.DefaultFormats

  def extractJsonToObject(jsonString: String): T = {
    val json = JsonParser.parse(jsonString)
    json.extract[T]
  }
}