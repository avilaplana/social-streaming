package com.streaming.dashboard.actor

import akka.actor._
import scala.concurrent.duration._

import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.pattern.ask

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

object Master {

  implicit val timeout = akka.util.Timeout(1 second)

  lazy val default = {
    val master = Akka.system.actorOf(Props[Master])
    master ! StartConsumer
    master
  }

  def filterByLanguage(language: Option[String]) {
    default ! FilterByLanguage(language)
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

class Master() extends Actor {

  val consumerActor = Akka.system.actorOf(Props(new Consumer(self)), "consumer")
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

    case StartConsumer => consumerActor ! StartConsumer

    case FilterByLanguage(language) => {
      consumerActor ! FilterByLanguage(language)
    }

    case twitterEvent: TwitterEvent =>
      val msg = JsObject(
        Seq(
          "kind" -> JsString("talk"),
          "user" -> JsString("alvaro"),
          "tweet" -> JsString(twitterEvent.text),
          "created_at" -> JsString(twitterEvent.created_at.toString),
          "lang" -> JsString(twitterEvent.lang),
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

case class StartConsumer() extends Message

case class Join(username: String)

case class Quit(username: String)

case class Talk(username: String, text: String)

case class Connected(enumerator: Enumerator[JsValue])

case class CannotConnect(msg: String)

case class FilterByLanguage(language: Option[String])
