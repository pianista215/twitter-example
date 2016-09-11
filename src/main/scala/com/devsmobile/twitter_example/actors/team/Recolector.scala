package com.devsmobile.twitter_example.actors.team

import akka.actor.Actor
import com.devsmobile.twitter_example.actors.team.Recolector.{NewAggregation, StartAggregation, TweetReceived}
import com.devsmobile.twitter_example.common.TwitterExUtils
import com.devsmobile.twitter_example.reader.Tweet
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

/**
  * Created by usarasola on 11/09/16.
  */
abstract class Recolector extends Actor with LazyLogging{

  import context._

  protected val window = 30 seconds

  def termsToLook: List[String]

  def tweetMatched(tweet: Tweet): Unit

  def finishedPeriodAggregation: Unit

  override final def receive: Receive = {
    case StartAggregation =>
      context.system.scheduler.scheduleOnce(window, self, NewAggregation)

    case NewAggregation =>
      logger.debug("New period aggregation")
      finishedPeriodAggregation
      context.system.scheduler.scheduleOnce(window, self, NewAggregation)

    case TweetReceived(tweet) =>
      if(isTweetMatching(tweet)){
        tweetMatched(tweet)
      }
  }

  /**
    * Is tweet related with the team??
    * Also prevent that "lions" could be confused with "lio", It must be the word
    *
    * @param tweet
    * @return
    */
  protected def isTweetMatching(tweet: Tweet): Boolean =
    termsToLook.view exists { term =>
      TwitterExUtils.withoutDiacriticsToLower(tweet.msg).contains(s" ${TwitterExUtils.withoutDiacriticsToLower(term)} ")
    }

}

object Recolector {
  case class TweetReceived(tweet: Tweet)
  case object StartAggregation
  case object NewAggregation
}