name := "twitter-example"

version := "1.0"

scalaVersion := "2.11.8"

lazy val hbcVersion = "2.2.0"
lazy val slf4jVersion = "1.7.21"
lazy val typeSafeConfigVersion = "1.3.0"
lazy val json4sVersion = "3.4.0"

libraryDependencies ++= Seq(
  "com.twitter" % "hbc-core" % hbcVersion,
  "org.slf4j" % "slf4j-simple" % slf4jVersion,
  "com.typesafe" % "config" % typeSafeConfigVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion
)
    