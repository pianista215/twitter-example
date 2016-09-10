package com.devsmobile.twitter_example.reader

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.devsmobile.twitter_example.reader.TwitterReader.Start
import com.typesafe.scalalogging.LazyLogging
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.jackson.JsonMethods._

/**
  * Created by pianista on 31/08/16.
  */
class TwitterReader extends HbcClient with Actor with LazyLogging {

  override def receive: Receive = {
    case Start(terms) =>
      logger.info(s"Starting listening tweets for: ${terms mkString(",")}.")
      val (client, queue) = startListeningFor(terms)
      val queueConsumer = context.system.actorOf(Props[QueueConsumer], name = "queueConsumer")
      queueConsumer ! QueueConsumer.StartConsumingFrom(queue)
  }

}

object TwitterReader {
  case class Start(terms: List[String])
}
