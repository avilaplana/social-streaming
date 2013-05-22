package controllers

import play.api.mvc._

import play.api.libs.json._


import concurrent.Future
import play.api.libs.ws.WS
import play.api.libs.ws
import java.util.UUID
import com.streaming.dashboard.actor.Master

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

  def startFilter(filter: Option[String]) = Action {
    implicit request =>
      filter.filterNot(_.isEmpty).map {
        username =>
          val result: Future[ws.Response] = {
            WS.url(s"http://localhost:8080/filter/start/$username").post("")
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
