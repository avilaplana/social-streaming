name := "filter"

version := "1.0"

scalaVersion := "2.10.0"

seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.0" % "compile"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.2" % "compile"

libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.2.3" % "compile"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.1.0" % "compile"

libraryDependencies += "net.databinder" % "dispatch-oauth_2.10" % "0.8.9" % "compile"

libraryDependencies += "net.databinder" % "unfiltered-filter_2.10" % "0.6.8" % "compile"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.4" % "compile"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.4" % "compile"

libraryDependencies += "log4j" % "log4j" % "1.2.17" % "compile"

libraryDependencies += "net.liftweb" % "lift-json_2.10" % "2.5-M4" % "compile"

libraryDependencies +=  "com.rabbitmq" % "amqp-client" % "3.0.4" % "compile"

libraryDependencies += "org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0" % "compile"

libraryDependencies += "org.specs2" % "specs2_2.10" % "1.13" % "test"

