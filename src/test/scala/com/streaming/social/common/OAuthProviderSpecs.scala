package com.streaming.social.common

import org.specs2.mutable.Specification


class OAuthProviderSpecs extends Specification{

  val oauth = new OAuthProvider
  "get oAuth header" should {
      "success" in {
        val oauthHeader = oauth.getOAuthHeader()
        oauthHeader.get("Authorization").isDefined must beTrue
      }
    }

}