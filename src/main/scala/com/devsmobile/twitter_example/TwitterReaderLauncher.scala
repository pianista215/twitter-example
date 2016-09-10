package com.devsmobile.twitter_example

import akka.actor.{ActorSystem, Props}
import com.devsmobile.twitter_example.actors.TwitterReader
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by pianista on 31/08/16.
  */
object TwitterReaderLauncher extends App with LazyLogging {

  logger.info("Starting Actor System and Actors")
  val system = ActorSystem("MyActorSystem")

  val twitterReader = system.actorOf(Props[TwitterReader], name = "initialActor")
  twitterReader ! TwitterReader.Start(List("Barcelona"))

}
