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
import collection.mutable.ListBuffer
import com.streaming.dashboard.rest.{FilterHttpRequester, FilterAdapter}
import com.streaming.dashboard.registry
import java.net.URLEncoder

object Master extends Logging {

  implicit val timeout = akka.util.Timeout(1 second)

  lazy val default = {
    val master = Akka.system.actorOf(Props(new Master(registry.filterStrategy)))
    master ! StartConsumer
    master
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

class Master(filterStrategy: FilterAdapter[String]) extends Actor with Logging {

  val consumerActor = Akka.system.actorOf(Props(new Consumer(self)), "consumer")
  var connected = Map.empty[String, Concurrent.Channel[JsValue]]
  var filterMap = Map.empty[String, ListBuffer[String]]
  var languageMap = Map.empty[String, String]

  def receive = {

    case Join(username) => {
      val e = Concurrent.unicast[JsValue] {
        c =>
          connected = connected + (username -> c)
      }
      sender ! Connected(e)
    }

    case Quit(username) => {

      connected = connected - username
      languageMap = languageMap - username
      val filterEntry = filterMap.filter(entry => entry._2.contains(username))
      debug(s"Username $username quitted,removing data from $filterMap, $languageMap")
      filterEntry.foreach {
        entry =>
          filterStrategy.removeFilter(entry._1)
          val usernames = entry._2
          if (entry._2.size > 1) filterMap = filterMap + (entry._1 -> (usernames -= username))
          else filterMap = filterMap - entry._1
      }



    }

    case StartConsumer => consumerActor ! StartConsumer

    case AddFilter(username, filter, language) => {
      filterMap.get(filter) match {
        case Some(usernames) if !(usernames.contains(username)) => filterMap = filterMap + (filter -> (usernames += username))
        case Some(usernames) if (usernames.contains(username)) => warn(s"Username: $username has already filter $filter")
        case None => filterMap = filterMap + (filter -> ListBuffer(username))
      }

      language match {
        case Some(lang) => {
          languageMap = languageMap + (username -> lang)
        }
        case _ =>
      }
    }

    case RemoveFilter(username, filter) => {
      filterMap.get(filter) match {
        case Some(usernames) if (usernames.contains(username) && usernames.size == 1) => filterMap = filterMap - filter
        case Some(usernames) if (usernames.contains(username) && usernames.size > 1) => filterMap = filterMap + (filter -> (usernames -= username))
        case Some(usernames) if !(usernames.contains(username)) => warn(s"Username: $username has no a filter $filter")
        case _ => debug("")
      }
      languageMap = languageMap - username
    }

    case twitterEvent: TwitterEvent =>
      val firstRoundCandidates = filterMap.filter(element => twitterEvent.text.toLowerCase().contains(element._1.toLowerCase()))
      val flattenList = firstRoundCandidates.values.flatten
      val secondRoundCandidates = flattenList.filter {
        username => info(username); (languageMap.get(username).isEmpty || languageMap.get(username).get == twitterEvent.lang.getOrElse(null))
      }

      val msg = JsObject(
        Seq(
          "user" -> JsString("alvaro"),
          "tweet" -> JsString(twitterEvent.text),
          "created_at" -> JsString(twitterEvent.created_at.toString),
          "lang" -> JsString(twitterEvent.lang.getOrElse("undefined")),
          "tweeterUser" -> JsString(twitterEvent.user.screen_name),
          "url" -> JsString(twitterEvent.user.profile_image_url),
          "followers_count" -> JsString(twitterEvent.user.followers_count.toString),
          "friends_count" -> JsString(twitterEvent.user.friends_count.toString)
        )
      )
      secondRoundCandidates.foreach(username => connected.get(username).get.push(msg))


  }
}

case class StartConsumer() extends Message

case class Join(username: String)

case class Quit(username: String)

case class Talk(username: String, text: String)

case class Connected(enumerator: Enumerator[JsValue])

case class CannotConnect(msg: String)

case class AddFilter(username: String, filter: String, language: Option[String] = None)

case class RemoveFilter(username: String, filter: String)
