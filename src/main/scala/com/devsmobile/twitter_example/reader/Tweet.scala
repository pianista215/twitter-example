package com.devsmobile.twitter_example.reader

import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
  * Created by pianista on 6/09/16.
  */
case class Tweet(msg: String, timestamp: Long)

object Tweet {
  def apply(json: String): Tweet = {
    val parsed = parse(json)
    val JString(msgValue) = parsed \ "text"
    val JString(timestampValue) = parsed \ "timestamp_ms"
    Tweet(msgValue, timestampValue.toLong)
  }
}
