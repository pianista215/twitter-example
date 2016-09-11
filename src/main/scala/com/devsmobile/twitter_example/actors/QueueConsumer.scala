package com.devsmobile.twitter_example.actors

import java.util.concurrent.LinkedBlockingQueue

import akka.actor.{Actor, ActorRef, Props}
import com.devsmobile.twitter_example.actors.QueueConsumer.{ContinueConsumingFrom, StartConsumingFrom}
import com.devsmobile.twitter_example.actors.team.Recolector.StartAggregation
import com.devsmobile.twitter_example.actors.team._
import com.devsmobile.twitter_example.common.{Team, TwitterExUtils}
import com.devsmobile.twitter_example.reader.Tweet
import com.typesafe.scalalogging.LazyLogging
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.jackson.JsonMethods._

import scala.concurrent.duration._
/**
  * Created by usarasola on 10/09/16.
  */
class QueueConsumer extends Actor with LazyLogging {

  import context._

  override def receive: Receive = {
    case StartConsumingFrom(queue,team) =>
      logger.info("Creating child actors for generic, coach, president and players")
      val recolectors = launchTeamRecolectors(team)
      recolectors map (_ ! StartAggregation)
      logger.info("Changing to state consuming")
      self ! ContinueConsumingFrom(queue,recolectors)
      context.become(consuming)
  }

  private def launchTeamRecolectors(team: Team): List[ActorRef] = {
    val teamRecolector = context.system.actorOf(Props(classOf[TeamRecolector], team), "TeamRecolector")
    val presidentRecolector = context.system.actorOf(Props(classOf[PresidentRecolector], team.name, team.president), "PresidentRecolector")
    val coachRecolector = context.system.actorOf(Props(classOf[CoachRecolector], team.name, team.coach), "CoachRecolector")
    val playersRecolectors = team.players map { player =>
      context.system.actorOf(Props(classOf[PlayerRecolector], team.name, player), player.name)
    }
    teamRecolector :: presidentRecolector :: coachRecolector :: playersRecolectors
  }

  private def consuming: Receive = {
    case ContinueConsumingFrom(queue, recolectors) =>
      if(queue.isEmpty) { //Wait until some tweet arrives
        logger.info("Empty queue, waiting to messages")
        context.system.scheduler.scheduleOnce(1000 milliseconds, self, ContinueConsumingFrom(queue, recolectors))
      } else {
        val rawTweet = queue.take()
        parseTweet(rawTweet) map { tweet =>
          logger.debug(s"Tweet: $tweet")

          if(isFootballTweet(tweet)) {
            logger.debug(s"It's a football tweet: $tweet")
            recolectors map (_ ! Recolector.TweetReceived(tweet))
          }
        }
        self ! ContinueConsumingFrom(queue, recolectors)
      }
  }


  /**
    * Check if the tweet is related to football
    *
    * @return
    */
  private def isFootballTweet(tweet: Tweet): Boolean =
    TwitterExUtils.genericFootballTerms.view exists { term =>
      TwitterExUtils.withoutDiacriticsToLower(tweet.msg).contains(TwitterExUtils.withoutDiacriticsToLower(term))
    }



  protected def parseTweet(json: String): Option[Tweet] = {
    val parsed = parse(json)
    for {
      JObject(_) <- parsed.toOption
      JString(msgValue) <- (parsed \ "text").toOption
      JString(timestampValue) <- (parsed \ "timestamp_ms").toOption
    } yield Tweet(msgValue, timestampValue.toLong)
  }
}

object QueueConsumer {
  //Receive mode
  case class StartConsumingFrom(queue: LinkedBlockingQueue[String], team: Team)

  //Consuming mode
  case class ContinueConsumingFrom(queue: LinkedBlockingQueue[String], recolectors : List[ActorRef])
}
