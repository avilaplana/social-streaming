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
import com.streaming.dashboard.rest.FilterAdapter
import com.streaming.dashboard.registry
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import scala.Some
import play.api.libs.json.JsObject

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
  val recommendationActor = Akka.system.actorOf(Props(new RecommendationsConsumer(self)), "recommendationConsumer")
  var connected = Map.empty[String, Concurrent.Channel[JsValue]]
  var filterMap = Map.empty[String, ListBuffer[String]]
  var locationMap = Map.empty[String, String]
  var languageMap = Map.empty[String, String]
  var followersMap = Map.empty[String, String]
  val (chatEnumerator, chatChannel) = Concurrent.broadcast[JsValue]

  def receive = {

    case Join(username) => {
      val e = Concurrent.unicast[JsValue] {
        c =>
          connected = connected + (username -> c)
      }
      sender ! Connected(e)
    }

    case Quit(username) => {

      var sent = false
      connected = connected - username
      languageMap = languageMap - username

      if (locationMap.contains(username)) {
        filterStrategy.removeLocation(locationMap.get(username).get)
        sent = true
      }
      locationMap = locationMap - username

      val filterCandidates = filterMap.filter(entry => entry._2.contains(username))
      if (filterCandidates.size > 1) warn(s"Too many filters to remove for the same username $username")

      debug(s"Username $username quitted,removing data from $locationMap $filterMap, $languageMap, $followersMap")
      filterCandidates.foreach {
        entry =>
          if (!sent) filterStrategy.removeFilter(entry._1)
          val usernames = entry._2
          if (entry._2.size > 1) filterMap = filterMap + (entry._1 -> (usernames -= username))
          else filterMap = filterMap - entry._1
      }
    }

    case StartConsumer => consumerActor ! StartConsumer; recommendationActor ! StartConsumer

    case AddFilter(username, filter, followers, language, location) => {
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

      followers match {
        case Some(number) => {
          followersMap = followersMap + (username -> number)
        }
        case _ =>
      }

      location match {
        case Some(loc) => {
          locationMap = locationMap + (username -> loc.toUpperCase)
        }
        case _ =>
      }
    }

    case RemoveFilter(username, filter, location) => {
      filterMap.get(filter) match {
        case Some(usernames) if (usernames.contains(username) && usernames.size == 1) => filterMap = filterMap - filter
        case Some(usernames) if (usernames.contains(username) && usernames.size > 1) => filterMap = filterMap + (filter -> (usernames -= username))
        case Some(usernames) if !(usernames.contains(username)) => warn(s"Username: $username has no a filter $filter")
        case _ => warn(s"No filter $filter defined")
      }

      languageMap = languageMap - username
      followersMap = followersMap - username
      locationMap = locationMap - username
    }

    case twitterEvent: TwitterEvent => {

      //      debug(s"Ready to filter $twitterEvent")
      val firstRoundCandidates = filterMap.filter(element => twitterEvent.text.toLowerCase().contains(element._1.toLowerCase()))
      val flattenList = firstRoundCandidates.values.flatten
      val secondRoundCandidates = flattenList.filter {
        username => info(username); (languageMap.get(username).isEmpty || languageMap.get(username).get == twitterEvent.lang.getOrElse(null))
      }

      val thirdRoundCandidates = secondRoundCandidates.filter {
        username => (followersMap.get(username).isEmpty || isFilteredByFollowers(followersMap.get(username).get, twitterEvent.user.followers_count))
      }

      val forthRoundCandidates = thirdRoundCandidates.filter {
        username => ((locationMap.contains(username) &&
          twitterEvent.place.isDefined &&
          twitterEvent.place.get.country_code.isDefined && locationMap.get(username).get.equals(twitterEvent.place.get.country_code.get))
          || !locationMap.contains(username))
      }

      val lang: Option[String] = twitterEvent.lang match {
        case Some(language) => registry.mapLanguages.get(language)
        case _ => None
      }

      val msg = JsObject(
        Seq(
          "user" -> JsString("alvaro"),
          "tweet" -> JsString(twitterEvent.text),
          "created_at" -> JsString(twitterEvent.created_at.toString),
          "lang" -> JsString(lang.getOrElse("undefined")),
          "tweeterUser" -> JsString(twitterEvent.user.screen_name),
          "url" -> JsString(twitterEvent.user.profile_image_url),
          "followers_count" -> JsString(twitterEvent.user.followers_count.toString),
          "friends_count" -> JsString(twitterEvent.user.friends_count.toString)
        )
      )
      forthRoundCandidates.foreach(username => connected.get(username).get.push(msg))
    }

    case recommendations: Recommendations => {
      if (recommendations.listRecommendationds.size > 0) {
        val msg = JsObject(
          "recommendations" -> JsArray(
            JsObject(
              "language" -> JsString("es") ::
                "candidates" -> JsArray(JsString("test1") :: JsString("test2") :: Nil)
                :: Nil)
              ::
              JsObject(
                "language" -> JsString("en") ::
                  "candidates" -> JsArray(JsString("test1") :: JsString("test2") :: Nil)
                  :: Nil
              ) :: Nil
          ) :: Nil
        )
        debug(s"About to send the recommendation $msg to $chatChannel")
        //todo this needs to be sent in broadcast
        connected.values.foreach(channel => channel.push(msg))
      }
    }

  }

  private def isFilteredByFollowers(followersFilter: String, followers: Integer) = {
    val filtered = followersFilter match {
      case "1" if (followers > 500) => true
      case "2" if (followers > 5000) => true
      case "3" if (followers > 50000) => true
      case "4" if (followers > 1000000) => true
      case _ => false
    }
    filtered
  }

}

case class StartConsumer() extends Message

case class Join(username: String)

case class Quit(username: String)

case class Talk(username: String, text: String)

case class Connected(enumerator: Enumerator[JsValue])

case class CannotConnect(msg: String)

case class AddFilter(username: String, filter: String, followers: Option[String] = None, language: Option[String] = None, location: Option[String] = None)

case class RemoveFilter(username: String, filter: String, location: Option[String] = None)
