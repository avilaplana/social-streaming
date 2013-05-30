package com.streaming.dashboard

import common.Logging
import com.typesafe.config.ConfigFactory
import rest.FilterHttpRequester


object ComponentRegistry {
  def apply() = new ComponentRegistry
}

class ComponentRegistry extends Logging {

//  val conf = ConfigFactory.load("application.conf")
  val filterStrategy = FilterHttpRequester("http://localhost:8080/filter/")

}