package com.devsmobile.twitter_example.elasticsearch

import com.devsmobile.twitter_example.reader.Tweet

import scala.concurrent.Future

/**
  * Created by pianista on 7/09/16.
  */
trait TweetSaver {
  def save(tweet: Tweet): Future[Unit]
}
