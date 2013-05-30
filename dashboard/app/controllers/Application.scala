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
import com.streaming.dashboard.registry
object Application extends Controller with Logging {

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
   * Handles the websocket.
   */
  def stream(username: String) = WebSocket.async[JsValue] {
    request =>
      info(s"Websocket connection with id is $username")
      Master.join(username)

  }

  def startFilter(username: Option[String], filter: Option[String], language: Option[String] = None) = Action {
    implicit request =>
      val valueToFilterBy = filter.getOrElse(throw new RuntimeException)
      registry.filterStrategy.addFilter(valueToFilterBy)
      Master.default ! AddFilter(username.get, valueToFilterBy, language)
      Ok("Adding..........")

  }

  def stopFilter(username: Option[String], filter: Option[String]) = Action {
    implicit request =>
      val valueToFilterBy = filter.getOrElse(throw new RuntimeException)
      registry.filterStrategy.removeFilter(valueToFilterBy)
      Master.default ! RemoveFilter(username.get, valueToFilterBy)
      Ok("Removing..........")
  }

}
