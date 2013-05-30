package com.streaming.dashboard.rest

import concurrent.Future
import play.api.libs.ws
import play.api.libs.ws.WS
import java.net.URLEncoder


trait FilterAdapter[T] {
  def addFilter(filter: T)

  def removeFilter(filter: T)
}

class FilterHttpRequester(url: String) extends FilterAdapter[String] {
  def addFilter(filter: String) {
    val encodedItem = URLEncoder.encode(filter, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"http://localhost:8080/filter/add/$encodedItem").post("")
    }
  }

  def removeFilter(filter: String) {
    val encodedItem = URLEncoder.encode(filter, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"http://localhost:8080/filter/remove/$encodedItem").post("")
    }
  }
}