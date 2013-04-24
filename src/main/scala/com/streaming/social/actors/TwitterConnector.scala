package com.streaming.social.actors

import scala.RuntimeException
import java.io.{InputStreamReader, InputStream, BufferedReader}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import akka.actor.Actor
import com.streaming.social.common.Http._
import com.streaming.social.common.{OAuthProvider, Json}
import akka.event.Logging


class TwitterConnector(url: String, oAuthProvider: OAuthProvider) extends Actor {

  val log = Logging(context.system, this)
  val httpClient = new DefaultHttpClient


  override def postStop() {
    super.postStop()
    log.info("connector Stopped")
  }

  def receive = {
    case StartSream(track) => streamByCriteria(track: String)
    case Finish => log.info("connector is terminating")
    case _ => log.error("Action not valid")
  }


  private def streamByCriteria(track: String) {
    try {
      val httpPost = new HttpPost(url)
      addHeader(httpPost, oAuthProvider.getOAuthHeader())
      val httpResponse = httpClient.execute(addValuePairToBody(httpPost, List(("track", track))))
      httpResponse.getStatusLine.getStatusCode match {
        case 200 => extractBody(httpResponse.getEntity.getContent);
        case _ => throw new RuntimeException("Status code: " + httpResponse.getStatusLine)
      }
    }
    finally {
      httpClient.getConnectionManager.shutdown
    }
  }

  private def extractBody(instream: InputStream) {
    var br: BufferedReader = new BufferedReader(new InputStreamReader(instream))
    while (true) {
      Option(br.readLine) match {
        case Some(tweet) if (tweet.trim.length == 0 || tweet.contains("\"limit\":{\"track\"")) => log.warning("Ignore it")
        case Some(tweet) => {
          sender ! Tweet(tweet)
        }
        case None => throw new RuntimeException("The content coming from Twitter is null")
      }
    }
  }
}


