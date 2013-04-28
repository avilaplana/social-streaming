package com.streaming.social.actors

import java.io.InputStream
import java.util.Date


trait Message

case class StartSream(parameters: Map[String, String]) extends Message
case class StopStream() extends Message
case class ReadStream(stream: InputStream) extends Message
case class Tweet(tweet : String) extends Message
case class TwitterEvent(val text: String, val user: User) extends Message
case class User(val profile_image_url: String)