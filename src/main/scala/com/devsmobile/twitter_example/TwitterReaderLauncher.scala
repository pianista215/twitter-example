package com.devsmobile.twitter_example

import akka.actor.{ActorSystem, Props}
import com.devsmobile.twitter_example.actors.TwitterReader
import com.devsmobile.twitter_example.common._
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import com.typesafe.scalalogging.LazyLogging

import collection.JavaConverters._

/**
  * Created by pianista on 31/08/16.
  */
object TwitterReaderLauncher extends App with LazyLogging {

  //Init
  {
    logger.info("Starting Actor System and Actors")
    val system = ActorSystem("MyActorSystem")

    val team = TwitterExUtils.config.getString("listener.team")
    logger.info(s"Team: $team")

    val teamTermsConfig = ConfigFactory.load(s"teams/$team.conf").getConfig("tw")
    if (!teamTermsConfig.hasPath("general.name")) {
      logger.error(s"Not found config for team: $team")
      system.terminate()
    } else {
      val twitterReader = system.actorOf(Props[TwitterReader], name = "initialActor")
      twitterReader ! TwitterReader.Start(parseTeam(teamTermsConfig))
    }
  }



  def parseTeam(config: Config): Team = {
    val name = config.getString("general.name")
    val teamTerms = genericTeamTerms(config)
    val president = parsePresident(config)
    val coach = parseCoach(config)
    val players = parsePlayers(config)
    Team(name, teamTerms, president, coach, players)
  }


  private def genericTeamTerms(config: Config): List[String] =
    config.getString("general.name") :: ((config.getStringList("general.related") asScala) toList)

  private def parseCoach(config: Config): Coach =
    Coach(config.getString("team.coach.name"),
      (config.getStringList("team.coach.terms") asScala) toList)

  private def parsePresident(config: Config): President =
    President(config.getString("team.president.name"),
      (config.getStringList("team.president.terms") asScala) toList)

  private def parsePlayers(config: Config): List[Player] = {
    val playersConfig = config.getConfig("team.players")
    val playersEntries = playersConfig.entrySet() asScala

    playersEntries map {
      case entry: java.util.Map.Entry[String, ConfigValue]  =>
        val name = entry.getKey
        Player(name, (playersConfig.getStringList(name) asScala) toList)
    } toList
  }



}
