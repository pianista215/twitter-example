package com.devsmobile.twitter_example.reader

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.devsmobile.twitter_example.common.TwitterExConfig
import com.devsmobile.twitter_example.elasticsearch.ESClient
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.{Constants, Hosts, HttpHosts}
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.httpclient.auth.{Authentication, OAuth1}
import com.typesafe.scalalogging.LazyLogging
import org.json4s.JsonAST.{JObject, JString}
import org.json4s.jackson.JsonMethods._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by pianista on 31/08/16.
  */
abstract class HbcClient extends LazyLogging {

  val config = TwitterExConfig.config.getConfig("reader")

  def startListeningFor(terms: List[String]): (BasicClient, LinkedBlockingQueue[String]) = {
    val (client, queue) = setup(terms)
    start(client)
    (client,queue)
  }

  protected def start(hosebirdClient: BasicClient): Unit = {
    hosebirdClient.connect();
    logger.info("Connected to Twitter")
  }

  def stop(hosebirdClient: BasicClient): Unit =
    hosebirdClient.stop()

  protected def setup(terms: List[String]): (BasicClient, LinkedBlockingQueue[String]) = {
    val tweetQueue = new LinkedBlockingQueue[String](100000)

    import collection.JavaConverters._

    val hosebirdHosts = new HttpHosts(Constants.STREAM_HOST)
    val hosebirdEndpoint = new StatusesFilterEndpoint().trackTerms(terms asJava)

    val consumerKey = config.getString("consumerkey").trim
    val consumerSecret = config.getString("consumersecret").trim
    val token = config.getString("token").trim
    val tokenSecret = config.getString("tokensecret").trim

    logger.info(s"Consumer key : $consumerKey")
    logger.info(s"Consumer secret : $consumerSecret")
    logger.info(s"Token : $token")
    logger.info(s"Token secret : $tokenSecret")

    val hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, tokenSecret)

    val hosebirdClient = new ClientBuilder()
      .hosts(hosebirdHosts)
      .authentication(hosebirdAuth)
      .endpoint(hosebirdEndpoint)
      .processor(new StringDelimitedProcessor(tweetQueue)).build()

    (hosebirdClient, tweetQueue)
  }

}
