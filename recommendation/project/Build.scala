import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "recommendation"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.rabbitmq" % "amqp-client" % "2.8.1",
    jdbc,
    anorm
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
