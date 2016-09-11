package com.devsmobile.twitter_example.actors

import akka.actor.{Actor, Props}
import com.devsmobile.twitter_example.actors.TwitterReader.Start
import com.devsmobile.twitter_example.common.{Team, TwitterExUtils}
import com.devsmobile.twitter_example.reader.HbcClient
import com.typesafe.config.{Config, ConfigList, ConfigValue}
import com.typesafe.scalalogging.LazyLogging

import collection.JavaConverters._

/**
  * Created by pianista on 31/08/16.
  */
class TwitterReader extends HbcClient with Actor with LazyLogging {

  override def receive: Receive = {
    case Start(team) =>
      val termsToListen: List[String] = TwitterExUtils.genericFootballTerms//team.fullTerms
      logger.info(s"Starting listening tweets for: ${termsToListen mkString(",")}.")
      val (client, queue) = startListeningFor(termsToListen)
      val queueConsumer = context.system.actorOf(Props[QueueConsumer], name = "queueConsumer")
      queueConsumer ! QueueConsumer.StartConsumingFrom(queue,team)
  }


}

object TwitterReader {
  case class Start(team: Team)
}
