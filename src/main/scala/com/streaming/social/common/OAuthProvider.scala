package com.streaming.social.common

import dispatch.classic.oauth.{OAuth, Token, Consumer}
import java.net.URLEncoder


object OAuthProvider{
  def apply(method: String, urlToRequest: String, consumerKey: String, consumerSecret: String, oauthToken: String, oauthTokenSecrete: String) = {
    new OAuthProvider(method, urlToRequest, consumerKey, consumerSecret, oauthToken, oauthTokenSecrete)
  }
}

class OAuthProvider(method: String, urlToRequest: String, consumerKey: String, consumerSecret: String, oauthToken: String, oauthTokenSecrete: String) {

  def getOAuthHeader(parameters : Map[String, String])  = {
        val headers : Map[String,String] =  sign(parameters)
        val buffer = new StringBuilder()
        buffer.append("OAuth ")
        headers.init.foreach(entry => buffer.append(entry._1).append("=").append("\"").append(URLEncoder.encode(entry._2, "UTF-8")).append("\"").append(",").append(" "))
        buffer.append(headers.last._1).append("=").append("\"").append(URLEncoder.encode(headers.last._2, "UTF-8")).append("\"")
        Map("Authorization" -> buffer.toString())
      }

  private def sign(parameters : Map[String, String]) = {
    OAuth.sign(method,
      url = urlToRequest,
      user_params = parameters,
      consumer = Consumer(key = consumerKey,
                          secret = consumerSecret),
      token = Token(Map("oauth_token"-> oauthToken,
                        "oauth_token_secret"-> oauthTokenSecrete)),
      verifier = None,
      callback = None)
  }

}