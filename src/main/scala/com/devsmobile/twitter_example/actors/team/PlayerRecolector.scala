package com.devsmobile.twitter_example.actors.team

import java.util.Date

import com.devsmobile.twitter_example.actors.team.Recolector.{NewAggregation, TweetReceived}
import com.devsmobile.twitter_example.common.{Player, President}
import com.devsmobile.twitter_example.elasticsearch.ESClient
import com.devsmobile.twitter_example.reader.Tweet
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by usarasola on 11/09/16.
  */
class PlayerRecolector(teamName: String, player: Player) extends Recolector with LazyLogging {

  private var count: Long = 0
  private var time: Date = new Date()

  lazy val termsToLook: List[String] = player.terms

  def tweetMatched(tweet: Tweet): Unit = {
    logger.info(s"Tweets related to ${player.name}!! $tweet")
    count = count + 1
  }

  def finishedPeriodAggregation: Unit = {
    logger.info(s"Saving $count tweets since $time")
    ESClient.saveAggregation(teamName, player.name, count, time)
    count = 0
    time = new Date()
  }

}
