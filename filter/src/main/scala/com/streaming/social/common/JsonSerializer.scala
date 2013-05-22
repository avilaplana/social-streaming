package com.streaming.social.common

import net.liftweb.json._
import java.text.SimpleDateFormat


class JsonSerializer[A] {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy")
  }

  def extractObjectToJson(objectA: A) = {
    compact(render(Extraction.decompose(objectA)(formats)))

  }
}