package com.devsmobile.twitter_example.common

import java.text.Normalizer

import com.typesafe.config.{Config, ConfigFactory}

import collection.JavaConverters._

/**
  * Created by pianista on 6/09/16.
  */
object TwitterExUtils {

  val config : Config = ConfigFactory.load().getConfig("tw")


  def genericFootballTerms: List[String] =
    (config.getStringList("football.terms") asScala) toList

  def withoutDiacritics(str: String): String =
    Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
  /**
    * Remove non common characters from the string and convert úá to ua ...
    * @param str
    * @return
    */
  def withoutDiacriticsToLower(str: String): String =
    withoutDiacritics(str).toLowerCase


}
