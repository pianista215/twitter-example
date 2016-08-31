package com.devsmobile.twitter_example.reader

/**
  * Created by pianista on 31/08/16.
  */
trait TwitterClient {

  def startListeningFor(term: String): Unit
  def stop(): Unit

}
