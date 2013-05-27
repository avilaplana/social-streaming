package controllers

import play.api.mvc._

import play.api.libs.json._


import concurrent.Future
import play.api.libs.ws.WS
import play.api.libs.ws
import java.util.UUID
import com.streaming.dashboard.actor.{FilterByLanguage, Master}
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

  def startFilter(language: Option[String], filter: Option[String]) = Action {
    implicit request =>
      filter.filterNot(_.isEmpty).map {
        item =>
          Master.default ! FilterByLanguage(language)
          val result: Future[ws.Response] = {
            val encodedItem = URLEncoder.encode(item, "UTF-8")
            WS.url(s"http://localhost:8080/filter/start/$encodedItem").post("")
          }
          Ok("Starting..........")
      }.getOrElse {
        Ok("Bad..............")
      }
  }

  def stopFilter() = Action {
    implicit request =>
      val result: Future[ws.Response] = {
        WS.url(s"http://localhost:8080/filter/stop").post("")
      }
      Ok("Stop..........")
  }

}
