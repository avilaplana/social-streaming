import akka.actor.Props
import com.streaming.dashboard.actor.{StartConsumer, Master}
import play.api._
import libs.concurrent.Akka
import play.api.Play.current

object Global extends GlobalSettings {

  override def beforeStart(app: Application) {
    Master.default ! StartConsumer
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }


}