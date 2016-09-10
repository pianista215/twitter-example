package com.devsmobile.twitter_example.reader

import akka.actor.Actor
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
      startListeningFor(terms)
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

object TwitterReader {
  case class Start(terms: List[String])
}
