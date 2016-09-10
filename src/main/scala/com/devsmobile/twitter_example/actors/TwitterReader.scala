package com.devsmobile.twitter_example.actors

import akka.actor.{Actor, Props}
import com.devsmobile.twitter_example.actors.TwitterReader.Start
import com.devsmobile.twitter_example.reader.HbcClient
import com.typesafe.config.{Config, ConfigList, ConfigValue}
import com.typesafe.scalalogging.LazyLogging

import collection.JavaConverters._

/**
  * Created by pianista on 31/08/16.
  */
class TwitterReader extends HbcClient with Actor with LazyLogging {

  override def receive: Receive = {
    case Start(teamConfig) =>
      val termsToListen = obtainTermsToListen(teamConfig)
      logger.info(s"Starting listening tweets for: ${termsToListen mkString(",")}.")
      val (client, queue) = startListeningFor(termsToListen)
      val queueConsumer = context.system.actorOf(Props[QueueConsumer], name = "queueConsumer")
      queueConsumer ! QueueConsumer.StartConsumingFrom(queue)
  }

  private def obtainTermsToListen(config: Config): List[String] =
    genericFootballTerms(config) ::: genericTeamTerms(config) ::: coach(config) ::: president(config) ::: players(config)

  private def genericFootballTerms(config: Config): List[String] =
    (config.getStringList("football.terms") asScala) toList

  private def genericTeamTerms(config: Config): List[String] =
    config.getString("general.name") :: ((config.getStringList("general.related") asScala) toList)

  private def coach(config: Config): List[String] =
    (config.getStringList("team.coach") asScala) toList

  private def president(config: Config): List[String] =
    (config.getStringList("team.president") asScala) toList

  private def players(config: Config): List[String] = {
    val playersConfig = config.getConfig("team.players")
    val playersEntries = playersConfig.entrySet() asScala

    val termsPerPlayer = playersEntries map {
      case entry: java.util.Map.Entry[String, ConfigValue]  =>
        playersConfig.getStringList(entry.getKey) asScala
    } toList

    termsPerPlayer.flatten
  }


}

object TwitterReader {
  case class Start(teamTerms: Config)
}
