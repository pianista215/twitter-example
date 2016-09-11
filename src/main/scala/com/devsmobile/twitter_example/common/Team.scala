package com.devsmobile.twitter_example.common

/**
  * Created by usarasola on 10/09/16.
  */
case class Team(name: String,
                terms: List[String],
                president: President,
                coach: Coach,
                players: List[Player]) {

  /**
    * All twitter terms related with the team
    */
  lazy val fullTerms: List[String] =
    terms ::: president.terms ::: coach.terms ::: (players flatMap (_.terms))

}
