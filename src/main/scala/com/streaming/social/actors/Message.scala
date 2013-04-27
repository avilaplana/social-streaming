package com.streaming.social.actors

import java.io.InputStream


trait Message

case class StartSream(track: String) extends Message
case class StopStream() extends Message
case class ReadStream(stream: InputStream) extends Message
case class Tweet(tweet : String) extends Message
case class TwitterEvent(val text: String) extends Message