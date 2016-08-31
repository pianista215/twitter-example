package com.devsmobile.twitter_example

import com.devsmobile.twitter_example.reader.{HbcClient, TwitterClient}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by pianista on 31/08/16.
  */
object TwitterReaderLauncher extends App {

  val client: TwitterClient = new HbcClient

  client.startListeningFor("TODO")
  println("Launched. Press something to close.")

  scala.io.StdIn.readLine()

}
