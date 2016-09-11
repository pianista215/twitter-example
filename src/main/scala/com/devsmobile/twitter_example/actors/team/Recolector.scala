package com.devsmobile.twitter_example.actors.team

import akka.actor.Actor
import com.devsmobile.twitter_example.reader.Tweet

/**
  * Created by usarasola on 11/09/16.
  */
trait Recolector extends Actor {

}

object Recolector {
  case class TweetReceived(tweet: Tweet)
}