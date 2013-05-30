package com.streaming.dashboard.rest

import concurrent.Future
import play.api.libs.ws
import play.api.libs.ws.WS
import java.net.URLEncoder


trait FilterAdapter[T] {
  def addFilter(filter: T)

  def removeFilter(filter: T)
}

case class FilterHttpRequester(url: String) extends FilterAdapter[String] {
  def addFilter(filter: String) {
    val encodedItem = URLEncoder.encode(filter, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"${url}add/$encodedItem").post("")
    }
  }

  def removeFilter(filter: String) {
    val encodedItem = URLEncoder.encode(filter, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"${url}remove/$encodedItem").post("")
    }
  }
}