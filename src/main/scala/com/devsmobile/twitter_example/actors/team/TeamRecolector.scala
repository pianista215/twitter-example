package com.devsmobile.twitter_example.actors.team

import java.util.Date

import akka.actor.ActorRef
import com.devsmobile.twitter_example.actors.team.Recolector.{NewAggregation, TweetReceived}
import com.devsmobile.twitter_example.common.Team
import com.devsmobile.twitter_example.elasticsearch.ESClient
import com.devsmobile.twitter_example.reader.Tweet
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by usarasola on 11/09/16.
  */
class TeamRecolector(team: Team, childActors: List[ActorRef]) extends Recolector with LazyLogging {

  private var count: Long = 0
  private var time: Date = new Date()

  lazy val termsToLook: List[String] =
    team.fullTerms

  def tweetMatched(tweet: Tweet): Unit = {
    logger.info(s"Tweet related to team!! $tweet")
    count = count + 1
    childActors foreach (_ ! TweetReceived(tweet))
  }

  def finishedPeriodAggregation: Unit = {
    logger.info(s"Saving $count tweets since $time")
    ESClient.saveAggregation(team.name, team.name, count, time)
    count = 0
    time = new Date()
    childActors foreach (_ ! NewAggregation)
  }

}
