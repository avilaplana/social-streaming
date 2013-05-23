package controllers

import play.api.mvc._

import play.api.libs.json._


import concurrent.Future
import play.api.libs.ws.WS
import play.api.libs.ws
import java.util.UUID
import com.streaming.dashboard.actor.{FilterByLanguage, Master}
import java.net.URLEncoder

object Application extends Controller {

  /**
   * Just display the home page.
   */
  def index = Action {
    implicit request =>
      Ok(views.html.twitterFilter(UUID.randomUUID().toString))
  }

  /**
   * Handles the chat websocket.
   */
  def stream(socketIdentifier: String) = WebSocket.async[JsValue] {
    request =>
      Master.join(socketIdentifier)

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
