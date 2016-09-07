package com.devsmobile.twitter_example.reader

import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
  * Created by pianista on 31/08/16.
  */
trait TwitterClient {

  def startListeningFor(term: String): Unit
  def stop(): Unit


  def parseTweet(json: String): Option[Tweet] = {
    val parsed = parse(json)
    for {
      JObject(_) <- parsed.toOption
      JString(msgValue) <- (parsed \ "text").toOption
      JString(timestampValue) <- (parsed \ "timestamp_ms").toOption
    } yield Tweet(msgValue, timestampValue.toLong)
  }

}
