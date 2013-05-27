package controllers

import play.api.mvc._

import play.api.libs.json._


import concurrent.Future
import play.api.libs.ws.WS
import play.api.libs.ws
import java.util.UUID
import com.streaming.dashboard.actor.{RemoveFilter, AddFilter, Master}
import java.net.URLEncoder
import com.streaming.dashboard.common.Logging

object Application extends Controller with Logging {

  /**
   * Just display the home page.
   */
  def index = Action {
    implicit request =>
      Ok(views.html.index())
  }

  def socialRoom(username: Option[String]) = Action {
    implicit request =>
      username.filterNot(_.isEmpty).map {
        username =>
          Ok(views.html.socialRoom(username))
      }.getOrElse {
        Redirect(routes.Application.index).flashing(
          "error" -> "Please choose a valid username."
        )
      }
  }

  /**
   * Handles the chat websocket.
   */
  def stream(username: String) = WebSocket.async[JsValue] {
    request =>
      info(s"Websocket connection with id is $username")
      Master.join(username)

  }

  def startFilter(username: Option[String], filter: Option[String], language: Option[String] = None) = Action {
    implicit request =>
      val encodedItem = URLEncoder.encode(filter.get, "UTF-8")
      Master.default ! AddFilter(username.get, encodedItem, language)
      val result: Future[ws.Response] = {
        WS.url(s"http://localhost:8080/filter/add/$encodedItem").post("")
      }
      Ok("Starting..........")

  }

  def stopFilter(username: Option[String], filter: Option[String], language: Option[String] = None) = Action {
    implicit request =>
      val encodedItem = URLEncoder.encode(filter.get, "UTF-8")
      Master.default ! RemoveFilter(username.get, encodedItem, language)
      val result: Future[ws.Response] = {
        WS.url(s"http://localhost:8080/filter/remove/$encodedItem").post("")
      }
      Ok("Stop..........")
  }

}
