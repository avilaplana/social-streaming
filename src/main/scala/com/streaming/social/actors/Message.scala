package com.streaming.social.actors


trait Message

case class StartSream(track: String) extends Message
case class Finish() extends Message
case class Tweet(tweet : String) extends Message
case class TwitterEvent(val text: String) extends Message