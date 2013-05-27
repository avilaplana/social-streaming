package com.streaming.social.actors

import scala.RuntimeException
import java.io.{InputStreamReader, InputStream, BufferedReader}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpPost
import akka.actor.{ActorRef, Actor}
import com.streaming.social.common.Http._
import com.streaming.social.common.{Logging, OAuthProvider}
import collection.mutable
import java.util.UUID

class TwitterConnector(url: String, oAuthProvider: OAuthProvider, master: ActorRef) extends Actor with Logging {

  val httpClient = new DefaultHttpClient
  var sessionId: Option[String] = None

  var httpPost = new HttpPost(url)
  val filtersToApply = mutable.HashSet.empty[String]

  override def postStop() {
    super.postStop()
    info("connector Stopped")
  }

  def receive = {
    case AddFilter(parameters) => {
      val track = parameters.get("track").getOrElse(throw new RuntimeException)
      debug(s"Adding to the streaming $track to $filtersToApply")
      filtersToApply.isEmpty match {
        case false => {
          filtersToApply.contains(track.asInstanceOf[String].toLowerCase()) match {
            case false => {
              filtersToApply += track.asInstanceOf[String].toLowerCase()
              httpPost.releaseConnection()
              sessionId = Some(UUID.randomUUID().toString)
              streamByCriteria(Map("track" -> filtersToApply.mkString(",")), sessionId)
            }
            case true => debug(s"$track already exists in $filtersToApply")
          }
        }
        case true => {
          filtersToApply += track.asInstanceOf[String].toLowerCase()
          sessionId = Some(UUID.randomUUID().toString)
          streamByCriteria(Map("track" -> filtersToApply.mkString(",")), sessionId)
        }
      }
    }
    case RemoveFilter(parameters) => {
      val track = parameters.get("track").getOrElse(throw new RuntimeException)
      filtersToApply.contains(track.asInstanceOf[String].toLowerCase()) match {
        case true => {
          debug(s"Removing from the streaming $track from $filtersToApply")
          filtersToApply -= track.asInstanceOf[String].toLowerCase()
          val message = filtersToApply.isEmpty match {
            case true => StopStream
            case false => {
              httpPost.releaseConnection()
              sessionId = Some(UUID.randomUUID().toString)
              streamByCriteria(Map("track" -> filtersToApply.mkString(",")), sessionId)
            }
          }
          self ! message
        }
        case false => info(s"$track does not exists");
      }
    }

    case StopStream => {
      sessionId = None
      httpPost.releaseConnection()
      info("There are no more filters. Streaming close")
    }

    case ReadStream(stream, id) if (sessionId.isDefined && sessionId == id) => extractBody(stream, sessionId)

    case ReadStream(stream, id) if !(sessionId.isDefined && sessionId == id) => debug("Event in the queue to be discarded")

    case _ => error("Action not valid")
  }


  private def streamByCriteria(parameters: Map[String, String], sessionId: Option[String]) {

    addHeader(httpPost, oAuthProvider.getOAuthHeader(parameters))
    val httpResponse = httpClient.execute(addValuePairToBody(httpPost, parameters))
    httpResponse.getStatusLine.getStatusCode match {
      case 200 => self ! ReadStream(httpResponse.getEntity.getContent, sessionId)
      case _ => {
        httpClient.getConnectionManager.shutdown
        throw new RuntimeException("Status code: " + httpResponse.getStatusLine)
      }
    }
  }

  private def extractBody(instream: InputStream, sessionId: Option[String]) {
    var br: BufferedReader = new BufferedReader(new InputStreamReader(instream))
    Option(br.readLine) match {
      case Some(tweet) if (tweet.trim.length == 0 || tweet.contains("\"limit\":{\"track\"")) => warn("Ignore it")
      case Some(tweet) => {
        master ! Tweet(tweet)
        self ! ReadStream(instream, sessionId)
      }
      case None => {
        httpClient.getConnectionManager.shutdown
        throw new RuntimeException("The content coming from Twitter is null")
      }
    }
  }
}


