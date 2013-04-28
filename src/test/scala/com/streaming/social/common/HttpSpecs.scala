package com.streaming.social.common

import org.specs2.mutable.Specification
import org.apache.http.client.methods.HttpPost
import java.io.{InputStream, InputStreamReader, BufferedReader}
import com.streaming.social.common.Http._

class HttpSpecs extends Specification {

  "add parameters to body" should {
    "add set of key=value to body" in {
      val httpPost = new HttpPost()

      addValuePairToBody(httpPost, Map("track1"-> "value1", "track2"-> "value2"))
      val body = extractBody(httpPost.getEntity.getContent)
      body.contains("track1=value1") must beTrue
      body.contains("track2=value2") must beTrue
    }
  }

  private def extractBody(stream: InputStream) = {
    val inputStreamReader = new InputStreamReader(stream);
    val sb = new StringBuilder();
    val br = new BufferedReader(inputStreamReader);
    var read = br.readLine();

    while (read != null) {
      sb.append(read);
      read = br.readLine();
    }
    sb.toString()
  }
}