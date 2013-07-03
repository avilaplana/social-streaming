package com.streaming.social.actors

import java.io.InputStream
import java.util.Date


trait Message

case class AddFilter(parameters: Map[String, String]) extends Message
case class RemoveFilter(parameters: Map[String, String]) extends Message
case class RestartSream(parameters: Map[String, String]) extends Message
case class StopStream(filterToStop: String) extends Message
case class ReadStream(stream: InputStream, sessionId: Option[String]) extends Message
case class Tweet(tweet : String) extends Message
case class TwitterEvent(val text: String, val created_at: Date, val lang: Option[String], val user: User, val place : Option[Place]) extends Message
case class User(val name: String, val profile_image_url: String, val screen_name: String, val followers_count: Integer, val friends_count: Integer, val gender: Option[String] = None)
case class Place(val full_name : Option[String], val country_code : Option[String])
