package com.devsmobile.twitter_example.actors.team

import java.util.Date

import com.devsmobile.twitter_example.actors.team.Recolector.{NewAggregation, TweetReceived}
import com.devsmobile.twitter_example.common.{Coach, Team}
import com.devsmobile.twitter_example.elasticsearch.ESClient
import com.devsmobile.twitter_example.reader.Tweet
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by usarasola on 11/09/16.
  */
class CoachRecolector(teamName: String, coach: Coach) extends Recolector with LazyLogging {

  private var count: Long = 0
  private var time: Date = new Date()

  lazy val termsToLook: List[String] = coach.terms

  def tweetMatched(tweet: Tweet): Unit = {
    logger.info(s"Tweet related to coach ${coach.name}!! $tweet")
    count = count + 1
  }

  def finishedPeriodAggregation: Unit = {
    logger.info(s"Saving $count tweets since $time")
    ESClient.saveAggregation(teamName, coach.name, count, time)
    count = 0
    time = new Date()
  }

}
