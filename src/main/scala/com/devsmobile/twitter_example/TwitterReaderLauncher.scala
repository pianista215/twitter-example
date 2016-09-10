package com.devsmobile.twitter_example

import akka.actor.{ActorSystem, Props}
import com.devsmobile.twitter_example.actors.TwitterReader
import com.devsmobile.twitter_example.common.TwitterExConfig
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by pianista on 31/08/16.
  */
object TwitterReaderLauncher extends App with LazyLogging {

  logger.info("Starting Actor System and Actors")
  val system = ActorSystem("MyActorSystem")

  val team = TwitterExConfig.config.getString("listener.team")
  logger.info(s"Team: $team")

  val teamTermsConfig = ConfigFactory.load(s"teams/$team.conf")
  if(!teamTermsConfig.hasPath("general.name")){
    logger.error(s"Not found config for team: $team")
    system.terminate()
  } else {
    val twitterReader = system.actorOf(Props[TwitterReader], name = "initialActor")
    twitterReader ! TwitterReader.Start(teamTermsConfig withFallback(TwitterExConfig.config))
  }


}
