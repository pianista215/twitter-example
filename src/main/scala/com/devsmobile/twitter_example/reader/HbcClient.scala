package com.devsmobile.twitter_example.reader

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.{Constants, Hosts, HttpHosts}
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.{Authentication, OAuth1}

import collection.JavaConverters._
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by pianista on 31/08/16.
  */
class HbcClient extends TwitterClient {

  private def setup: Unit = {
    /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
    val msgQueue = new LinkedBlockingQueue[String](100000)
    val eventQueue = new LinkedBlockingQueue[Event](1000)

    /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
    // Optional: set up some followings and track terms
    val terms: List[String] = List("twitter", "api")

    val hosebirdHosts = new HttpHosts(Constants.STREAM_HOST)
    val hosebirdEndpoint = new StatusesFilterEndpoint().trackTerms(terms asJava)

    val hosebirdAuth = new OAuth1("consumerKey", "consumerSecret", "token", "secret")

    val hosebirdClient = new ClientBuilder()
      .name("Hosebird-Client-01")                              // optional: mainly for the logs
      .hosts(hosebirdHosts)
      .authentication(hosebirdAuth)
      .endpoint(hosebirdEndpoint)
      .processor(new StringDelimitedProcessor(msgQueue))
      .eventMessageQueue(eventQueue).build()                         // optional: use this if you want to process client events

    Future { //TODO
      // on a different thread, or multiple different threads....
      Thread.sleep(4000)


      while (!hosebirdClient.isDone()) {
        val msg = msgQueue.take()
        println(s"Leido $msg")
      }

    }

    // Attempts to establish a connection.
    hosebirdClient.connect();
    println("Connected")
  }

  override def startListeningFor(term: String): Unit = //TODO
    setup

  override def stop(): Unit = ???


}
