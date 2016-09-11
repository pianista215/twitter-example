package com.devsmobile.twitter_example.actors.team

import java.util.Date

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.devsmobile.twitter_example.actors.team.Recolector.TweetReceived
import com.devsmobile.twitter_example.actors.team.TeamRecolector.NewAggregation
import com.devsmobile.twitter_example.common.{Team, TwitterExUtils}
import com.devsmobile.twitter_example.reader.Tweet
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by usarasola on 11/09/16.
  */
class TeamRecolector(team: Team) extends Recolector with LazyLogging {

  private var count: Long = 0
  private var time: Date = new Date()

  override def receive: Receive = {
    case NewAggregation => logger.warn("TODO")
    case TweetReceived(tweet) =>
      if(isTeamTweet(tweet)){
        logger.info(s"Tweet related to team!! $tweet")
        count = count + 1
        logger.info(s"$count tweets since $time")
      } else logger.debug(s"Tweet no related for team $tweet")

  }

  /**
    * Is tweet related with the team??
    * Also prevent that "lions" could be confused with "lio", It must be the word
    * @param tweet
    * @return
    */
  private def isTeamTweet(tweet: Tweet): Boolean =
    team.fullTerms.view exists { term =>
      TwitterExUtils.withoutDiacriticsToLower(tweet.msg).contains(s" ${TwitterExUtils.withoutDiacriticsToLower(term)} ")
    }


}

object TeamRecolector {


  case object NewAggregation

}
