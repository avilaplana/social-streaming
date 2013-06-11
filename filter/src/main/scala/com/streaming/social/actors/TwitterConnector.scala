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
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}

class TwitterConnector(url: String, oAuthProvider: OAuthProvider, master: ActorRef) extends Actor with Logging {

  val httpClient = new DefaultHttpClient
  val creds = new UsernamePasswordCredentials(
    "alvarovilaplana", "RAFAEL80murcia");
  httpClient.getCredentialsProvider()
    .setCredentials(AuthScope.ANY, creds);
  var sessionId: Option[String] = None

  var httpPost = new HttpPost(url)
  val filtersToApply = mutable.HashMap.empty[String, Integer]

  override def postStop() {
    super.postStop()
    info("connector Stopped")
  }

  def receive = {
    case AddFilter(parameters) => {
      val (filterKey, newFilter) = getFilter(parameters)
      debug(s"Adding to the streaming $newFilter. to $filtersToApply")
      filtersToApply.isEmpty match {
        case false => {
          filtersToApply.get(newFilter.toLowerCase()) match {
            case Some(numberReq) => filtersToApply += (newFilter.toLowerCase() -> (numberReq + 1)); debug(s"$newFilter already exists in $filtersToApply")
            case None => {
              filtersToApply += (newFilter.toLowerCase() -> 1)
              httpPost.releaseConnection()
              sessionId = Some(UUID.randomUUID().toString)
              info(s"filter is $filtersToApply")
              streamByCriteria(buidParameters(filtersToApply.keySet), sessionId)
            }
          }
        }
        case true => {
          filtersToApply += (newFilter.toLowerCase() -> 1)
          sessionId = Some(UUID.randomUUID().toString)
          streamByCriteria(buidParameters(filtersToApply.keySet), sessionId)
        }
      }
    }
    case RemoveFilter(parameters) => {
      val (filterKey, newFilter) = getFilter(parameters)
      filtersToApply.get(newFilter.toLowerCase()) match {
        case Some(numberReq) => {
          if (numberReq > 1) {
            filtersToApply += (newFilter.toLowerCase() -> (numberReq - 1))
            debug(s"Substracting in 1 $newFilter from the streaming from $filtersToApply")
          } else {
            filtersToApply -= newFilter.toLowerCase()
            debug(s"Substracting $newFilter from the streaming from $filtersToApply")
          }

          val message = filtersToApply.isEmpty match {
            case true => StopStream
            case false => {
              httpPost.releaseConnection()
              sessionId = Some(UUID.randomUUID().toString)
              streamByCriteria(buidParameters(filtersToApply.keySet), sessionId)
            }
          }
          self ! message
        }
        case None => debug(s"$newFilter does not exists in $filtersToApply");
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

    //    addHeader(httpPost, oAuthProvider.getOAuthHeader(parameters))
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

  private def getFilter(parameters: Map[String, String]) = {
    if (parameters.get("track").isDefined) ("track", parameters.get("track").get.asInstanceOf[String])
    else if (parameters.get("locations").isDefined) ("locations", parameters.get("locations").get.asInstanceOf[String])
    else throw new RuntimeException()
  }

  private def buidParameters(filtersToApply:  scala.collection.Set[String]) = {
    val filterByLocations = filtersToApply.filter{element => element.contains(",")}
    val filterByStrings = filtersToApply.filterNot(element => element.contains(","))
    var parameters = collection.immutable.Map.empty[String, String]
    if (!filterByLocations.isEmpty) parameters = parameters + ("locations" -> filterByLocations.mkString(","))
    if (!filterByStrings.isEmpty) parameters = parameters + ("track" -> filterByStrings.mkString(","))
    debug(s"Parameters to be sent to Twitter $parameters")
    parameters

  }
}


