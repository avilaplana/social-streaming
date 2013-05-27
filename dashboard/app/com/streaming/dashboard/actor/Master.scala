package com.streaming.dashboard.actor

import akka.actor._
import scala.concurrent.duration._

import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import akka.pattern.ask

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import com.streaming.dashboard.common.Logging
import java.util.Date

object Master extends Logging {

  implicit val timeout = akka.util.Timeout(1 second)

  lazy val default = {
    val master = Akka.system.actorOf(Props[Master])
    master ! StartConsumer
    master
  }

  def filterByLanguage(language: Option[String]) {
    default ! FilterByLanguage(language)
  }

  def join(username: String): scala.concurrent.Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {

    (default ? Join(username)).map {

      case Connected(enumerator) =>
        // Create an Iteratee to consume the feed
        info(s"connected $username")
        val iteratee = Iteratee.foreach[JsValue] {
          event => info(s"Session with id $username")
        }.mapDone {
          _ => default ! Quit(username);
        }
        (iteratee, enumerator)

      case CannotConnect(error) => val iteratee = Done[JsValue, Unit]((), Input.EOF)
      // Send an error and close the socket
      val enumerator = Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))
      (iteratee, enumerator)
    }
  }
}

class Master() extends Actor with Logging {

  val consumerActor = Akka.system.actorOf(Props(new Consumer(self)), "consumer")
  var connected = Map.empty[String, Concurrent.Channel[JsValue]]

  def receive = {

    case Join(username) => {
      val e = Concurrent.unicast[JsValue] {
        c =>
          play.Logger.info("Start")
          connected = connected + (username -> c)
      }
      sender ! Connected(e)
    }

    case Quit(username) => {
      connected = connected - username
    }

    case StartConsumer => consumerActor ! StartConsumer

      // todo add here the username to apply filter to
      // todo the filter is in username level so the master is responsible of that
    case FilterByLanguage(language) => {
      consumerActor ! FilterByLanguage(language)
    }

    case twitterEvent: TwitterEvent =>
      val msg = JsObject(
        Seq(
          "user" -> JsString("alvaro"),
          "tweet" -> JsString(twitterEvent.text),
          "created_at" -> JsString(twitterEvent.created_at.toString),
          "lang" -> JsString(twitterEvent.lang.getOrElse("undefined")),
          "tweeterUser" -> JsString(twitterEvent.user.screen_name),
          "url" -> JsString(twitterEvent.user.profile_image_url)
        )
      )
      connected.get("alvaro").get.push(msg)
  }
}

case class StartConsumer() extends Message

case class Join(username: String)

case class Quit(username: String)

case class Talk(username: String, text: String)

case class Connected(enumerator: Enumerator[JsValue])

case class CannotConnect(msg: String)

case class FilterByLanguage(language: Option[String])
