package com.devsmobile.twitter_example.elasticsearch
import com.devsmobile.twitter_example.common.TwitterExConfig
import com.devsmobile.twitter_example.reader.Tweet
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.{LongType, StringType}
import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import org.elasticsearch.common.settings.Settings

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by pianista on 7/09/16.
  */
object ESClient extends TweetSaver {

  val config = TwitterExConfig.config.getConfig("elastic")

  val settings = Settings.settingsBuilder().put("cluster.name", config.getString("clustername")).build()
  val uri = ElasticsearchClientUri(config.getString("connectionurl"))
  private lazy val client = ElasticClient.transport(settings, uri)

  val indexName = "tweets"
  val mappingName = "tweet"
  val textField = "text"
  val timestampField = "timestamp"

  //Init
  {
    client.execute {
      create index indexName mappings (
        mappingName fields (
          textField typed StringType,
          timestampField typed LongType
          )
        )
    }
  }


  override def save(tweet: Tweet): Future[Unit] = client.execute {
    index into indexName / mappingName fields (
      textField -> tweet.msg,
      timestampField -> tweet.timestamp
      )
  } map { result => println(s"Saved $tweet") }

}
