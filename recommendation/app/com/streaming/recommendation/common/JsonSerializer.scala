package com.streaming.recommendation.common

import play.api.libs.json._


object JsonSerializer {

  def serializer(recommendations: Map[String, List[String]]) = {
    val a = recommendations.map {
      entry =>
        JsObject(Seq(("language" -> JsString(entry._1)),("candidates" -> JsArray(entry._2.map(candidate => JsString(candidate))))))
    }

    JsObject(
      "recommendations" -> JsArray(a.toSeq) :: Nil
    ).toString()
  }
}

