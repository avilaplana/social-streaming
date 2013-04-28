package com.streaming.social.actors

import scala.RuntimeException
import java.io.{InputStreamReader, InputStream, BufferedReader}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpPost
import akka.actor.{ActorRef, Actor}
import com.streaming.social.common.Http._
import com.streaming.social.common.{Logging, OAuthProvider}

class TwitterConnector(url: String, oAuthProvider: OAuthProvider, master: ActorRef) extends Actor with Logging {

  val httpClient = new DefaultHttpClient
  var idle = true
  val httpPost = new HttpPost(url)

  override def postStop() {
    super.postStop()
    info("connector Stopped")
  }

  def receive = {
    case StartSream(track) if idle => idle = false; streamByCriteria(track: String)
    case StopStream => idle = true; httpPost.releaseConnection()
    case ReadStream(stream) if !idle => extractBody(stream)
    case _ => error("Action not valid")
  }


  private def streamByCriteria(track: String) {

    addHeader(httpPost, oAuthProvider.getOAuthHeader(track))
    val httpResponse = httpClient.execute(addValuePairToBody(httpPost, List(("track", track))))
    httpResponse.getStatusLine.getStatusCode match {
      case 200 => self ! ReadStream(httpResponse.getEntity.getContent)
      case _ => {
        httpClient.getConnectionManager.shutdown
        throw new RuntimeException("Status code: " + httpResponse.getStatusLine)
      }
    }
  }

  private def extractBody(instream: InputStream) {
    var br: BufferedReader = new BufferedReader(new InputStreamReader(instream))
    Option(br.readLine) match {
      case Some(tweet) if (tweet.trim.length == 0 || tweet.contains("\"limit\":{\"track\"")) => warn("Ignore it")
      case Some(tweet) => {
        master ! Tweet(tweet)
        self ! ReadStream(instream)
      }
      case None => {
        httpClient.getConnectionManager.shutdown
        throw new RuntimeException("The content coming from Twitter is null")
      }
    }
  }
}


