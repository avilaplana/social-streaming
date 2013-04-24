package com.streaming.social.common

import dispatch.classic.oauth.{OAuth, Token, Consumer}
import java.net.URLEncoder


class OAuthProvider {

  def getOAuthHeader()  = {
        val headers : Map[String,String] =  sign
        val buffer = new StringBuilder()
        buffer.append("OAuth ")
        headers.init.foreach(entry => buffer.append(entry._1).append("=").append("\"").append(URLEncoder.encode(entry._2, "UTF-8")).append("\"").append(",").append(" "))
        buffer.append(headers.last._1).append("=").append("\"").append(URLEncoder.encode(headers.last._2, "UTF-8")).append("\"")
        Map("Authorization" -> buffer.toString())
      }

  private def sign = {
    OAuth.sign(method = "POST",
      url = "https://stream.twitter.com/1/statuses/filter.json",
      user_params = Map("track"->"obama"),
      consumer = Consumer(key = "TLHuWv1rcvqBGz5nf0jDw",
                          secret = "1CNlqxMRiiFMZvQgXTB6lLEaSqLpYXmnDZT02EQpIHk"),
      token = Token(Map("oauth_token"-> "78543968-dOUEFS3IGt6lfcStV6PbkGiOh3Wj7OKia7Uy7WZp3",
                        "oauth_token_secret"-> "sO7t721VWa3DK09dKn0DtbTD8RHPOGXVPq6fQTIuAw")),
      verifier = None,
      callback = None)
  }

}