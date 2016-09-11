package com.devsmobile.twitter_example.elasticsearch
import java.util.Date

import com.devsmobile.twitter_example.common.TwitterExUtils
import com.devsmobile.twitter_example.reader.Tweet
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{DateType, LongType, StringType}
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.typesafe.scalalogging.LazyLogging
import org.elasticsearch.common.settings.Settings

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by pianista on 7/09/16.
  */
object ESClient extends TweetSaver with LazyLogging{

  val config = TwitterExUtils.config.getConfig("elastic")

  val settings = Settings.settingsBuilder().put("cluster.name", config.getString("clustername")).build()
  val uri = ElasticsearchClientUri(config.getString("connectionurl"))
  private lazy val client = ElasticClient.transport(settings, uri)

  /*val indexName = "tweets"
  val mappingName = "tweet"
  val textField = "text"
  val timestampField = "timestamp"*/

  val indexName = "aggregations"
  val mappingName = "aggregation"
  val mainTeamField = "team"
  val nameField = "name"
  val countField = "count"
  val timestampField = "timestamp"

  //Init //TODO: Check if index exists....
  {
    client.execute {
      create index indexName mappings (
        mappingName fields (
          mainTeamField typed StringType,
          nameField typed StringType,
          countField typed LongType,
          timestampField typed DateType
          )
        )
    }
  }

  override def saveAggregation(team: String, name: String, count: Long, time: Date): Future[Unit] = client.execute {
    index into indexName / mappingName fields (
      mainTeamField -> team,
      nameField -> name,
      countField -> count,
      timestampField -> time
      )
  } map { result => logger.debug(s"Saved aggregation for $name") }


  /*override def save(tweet: Tweet): Future[Unit] = client.execute {
    index into indexName / mappingName fields (
      textField -> tweet.msg,
      timestampField -> new Date(tweet.timestamp)
      )
  } map { result => logger.debug(s"Saved $tweet") }*/
  //def save(tweet: Tweet): Future[Unit]

}
