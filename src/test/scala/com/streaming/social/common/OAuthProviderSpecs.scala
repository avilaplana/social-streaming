package com.streaming.social.common

import org.specs2.mutable.Specification
import com.streaming.social.registry._


class OAuthProviderSpecs extends Specification{


  "get oAuth header" should {
      "success" in {
        val oauthHeader = oauth.getOAuthHeader("obama")
        oauthHeader.get("Authorization").isDefined must beTrue
      }
    }

}