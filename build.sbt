name := "twitter-example"

version := "1.0"

scalaVersion := "2.11.8"

lazy val hbcVersion = "2.2.0"
lazy val slf4jVersion = "1.7.21"
lazy val typeSafeConfigVersion = "1.3.0"
lazy val json4sVersion = "3.4.0"
lazy val elasticSearchConnector = "2.3.0"
lazy val akkaVersion = "2.4.10"
lazy val scalaLoggingVersion = "3.4.0"

enablePlugins(DockerPlugin)

libraryDependencies ++= Seq(
  "com.twitter" % "hbc-core" % hbcVersion,

  //Logging
  "org.slf4j" % "slf4j-simple" % slf4jVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,

  "com.typesafe" % "config" % typeSafeConfigVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,

  //Elasticsearch
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elasticSearchConnector,

  //Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion

)
    