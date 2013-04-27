package com.streaming.social

import common.OAuthProvider


object ComponentRegistry {
  def apply() = new ComponentRegistry
}

class ComponentRegistry {
  val url = "https://stream.twitter.com/1/statuses/filter.json"
  val oauth = OAuthProvider(
    method = "POST",
    urlToRequest = url,
    consumerKey = "DEFINE CONSUMER KEY",
    consumerSecret = "DEFINE CONSUMER SECRET",
    oauthToken = "DEFINE OAUTH TOKEN",
    oauthTokenSecrete = "DEFINE OAUTH SECRET")
}