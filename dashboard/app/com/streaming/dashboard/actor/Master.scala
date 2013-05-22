package com.streaming.dashboard.actor

import akka.actor._
import scala.concurrent.duration._

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.util.Timeout
import akka.pattern.ask

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import com.streaming.dashboard.actor.{TwitterEvent, StartConsumer, Consumer}

object Master {

   implicit val timeout = akka.util.Timeout(1 second)

   lazy val default = {
     val master = Akka.system.actorOf(Props[Master])
     val consumerActor = Akka.system.actorOf(Props(new Consumer(master)), "consumer")
     consumerActor ! StartConsumer
     master
   }

   def join(socketIdentifier: String): scala.concurrent.Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {

     (default ? Join(socketIdentifier)).map {

       case Connected(enumerator) =>
         // Create an Iteratee to consume the feed
         val iteratee = Iteratee.foreach[JsValue] {
           event =>
             default ! Talk(socketIdentifier, (event \ "text").as[String])
         }.mapDone {
           _ =>
             default ! Quit(socketIdentifier)
         }
         (iteratee, enumerator)

       case CannotConnect(error) =>
         val iteratee = Done[JsValue, Unit]((), Input.EOF)
         // Send an error and close the socket
         val enumerator = Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))
         (iteratee, enumerator)
     }
   }
 }

class Master extends Actor {

   var sessions = Set.empty[String]
   val (chatEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

   def receive = {

     case Join(socketIdentifier) => {
       if (sessions.contains(socketIdentifier)) {
         sender ! CannotConnect("This username is already used")
       } else {
         sessions = sessions + socketIdentifier
         sender ! Connected(chatEnumerator)
       }
     }

     case Talk(socketIdentifier, text) => {
       notifyAll("talk", socketIdentifier, text)
     }

     case Quit(socketIdentifier) => {
       sessions = sessions - socketIdentifier
       notifyAll("quit", socketIdentifier, "has left the room")
     }

     case twitterEvent: TwitterEvent =>
       val msg = JsObject(
         Seq(
           "kind" -> JsString("talk"),
           "user" -> JsString("alvaro"),
           "tweet" -> JsString(twitterEvent.text),
           "created_at" -> JsString(twitterEvent.created_at.toString),
           "tweeterUser" -> JsString(twitterEvent.user.screen_name),
           "url" -> JsString(twitterEvent.user.profile_image_url),
           "members" -> JsArray(
             sessions.toList.map(JsString)
           )
         )
       )
       chatChannel.push(msg)
   }

   def notifyAll(kind: String, user: String, text: String) {
     val msg = JsObject(
       Seq(
         "kind" -> JsString(kind),
         "user" -> JsString(user),
         "message" -> JsString(text),
         "members" -> JsArray(
           sessions.toList.map(JsString)
         )
       )
     )
     chatChannel.push(msg)
   }

 }

case class Join(username: String)

case class Quit(username: String)

case class Talk(username: String, text: String)

case class Connected(enumerator: Enumerator[JsValue])

case class CannotConnect(msg: String)
