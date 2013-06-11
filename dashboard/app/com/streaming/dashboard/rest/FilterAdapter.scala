package com.streaming.dashboard.rest

import concurrent.Future
import play.api.libs.ws
import play.api.libs.ws.WS
import java.net.URLEncoder


trait FilterAdapter[T] {
  def addFilter(filter: T)

  def removeFilter(filter: T)

  def addLocation(location: T)

  def removeLocation(location: T)
}

case class FilterHttpRequester(url: String) extends FilterAdapter[String] {
  def addFilter(filter: String) {
    val encodedItem = URLEncoder.encode(filter, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"${url}filter/add/$encodedItem").post("")
    }
  }

  def removeFilter(filter: String) {
    val encodedItem = URLEncoder.encode(filter, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"${url}filter/remove/$encodedItem").post("")
    }
  }

  def addLocation(location: String) {
    val encodedItem = URLEncoder.encode(location, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"${url}location/add/$encodedItem").post("")
    }
  }

  def removeLocation(location: String) {
    val encodedItem = URLEncoder.encode(location, "UTF-8")
    val result: Future[ws.Response] = {
      WS.url(s"${url}location/remove/$encodedItem").post("")
    }
  }
}