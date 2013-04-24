package com.streaming.social.common

import org.apache.http.client.methods.HttpPost
import org.apache.http.{Header, NameValuePair}
import org.apache.http.message.{BasicHeader, BasicNameValuePair}
import java.net.URLEncoder
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.protocol.HTTP
import scala.collection.JavaConversions._


object Http {

  def addValuePairToBody(httpPost: HttpPost, pairs: List[Tuple2[String, String]]): HttpPost = {
    val parameters = scala.collection.mutable.ListBuffer[NameValuePair]()
    pairs.foreach(pair => parameters += new BasicNameValuePair(pair._1, URLEncoder.encode(pair._2, "UTF-8")))
    httpPost.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8))
    httpPost
  }

  def addHeader(httpPost: HttpPost, headers : Map[String,String]) = {
    headers.foreach(entry => httpPost.setHeader(createHeader(entry._1, entry._2)))
    httpPost
  }

  private def createHeader(key : String, value: String): Header = new BasicHeader(key, value)


}