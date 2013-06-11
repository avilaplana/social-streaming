package com.streaming.dashboard

import common.Logging
import com.typesafe.config.ConfigFactory
import rest.FilterHttpRequester


object ComponentRegistry {
  def apply() = new ComponentRegistry
}

class ComponentRegistry extends Logging {

  //  val conf = ConfigFactory.load("application.conf")
  val filterStrategy = FilterHttpRequester("http://localhost:8080/")
  val mapLanguages = Map("fr" -> "French",
    "en" -> "English",
    "ar" -> "Arab",
    "ja" -> "Japanese",
    "es" -> "Spanish",
    "de" -> "German",
    "it" -> "Italian",
    "id" -> "Indonesian",
    "pt" -> "Portugese",
    "ko" -> "Korean",
    "tr" -> "Turkish",
    "ru" -> "Russian",
    "nl" -> "Dutch",
    "fil" -> "Filipinesse",
    "msa" -> "Malaian",
    "zh-tw" -> "Tradional Chinesse",
    "zh-cn" -> "Simplified Chinesse",
    "hi" -> "Hindi",
    "no" -> "Norwegian",
    "sv" -> "Swedish",
    "fi" -> "Finish",
    "da" -> "Denish",
    "pl" -> "Polish",
    "hu" -> "Hungarian",
    "fa" -> "Persian",
    "he" -> "Hebreo",
    "ur" -> "Urdu",
    "th" -> "Thailand")


}