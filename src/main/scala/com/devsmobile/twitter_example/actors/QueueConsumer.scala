package com.devsmobile.twitter_example.actors

import java.util.concurrent.LinkedBlockingQueue

import akka.actor.Actor
import com.devsmobile.twitter_example.actors.QueueConsumer.{ContinueConsumingFrom, StartConsumingFrom}
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
    case StartConsumingFrom(queue) =>
      logger.info("Creating child actors for generic, coach, president and players")

      logger.info("Changing to state consuming")
      self ! ContinueConsumingFrom(queue)
      context.become(consuming)
  }

  private def consuming: Receive = {
    case ContinueConsumingFrom(queue) =>
      if(queue.isEmpty) { //Wait until some tweet arrives
        logger.info("Empty queue, waiting to messages")
        context.system.scheduler.scheduleOnce(1000 milliseconds, self, ContinueConsumingFrom(queue))
      } else {
        val rawTweet = queue.take()
        parseTweet(rawTweet) map { tweet =>
          logger.info(s"Tweet: $tweet")
        }
        self ! ContinueConsumingFrom(queue)
      }
  }


  /*Future { //To Actor??????
      Thread.sleep(4000)


      while (!hosebirdClient.isDone()) {
        logger.debug("Waiting")
        val msg = tweetQueue.take()
        parseTweet(msg) map { tweet =>
          logger.debug(s"Tweet: $tweet")
          ESClient.save(tweet)
        }
      }

      logger.debug("Done")

    }*/


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
  case class StartConsumingFrom(queue: LinkedBlockingQueue[String])

  //Consuming mode
  case class ContinueConsumingFrom(queue: LinkedBlockingQueue[String])
}
