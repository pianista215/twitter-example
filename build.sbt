name := "twitter-example"

version := "1.0"

scalaVersion := "2.11.8"

lazy val hbcVersion = "2.2.0"
lazy val slf4jVersion = "1.7.21"

libraryDependencies ++= Seq(
  "com.twitter" % "hbc-core" % hbcVersion,
  "org.slf4j" % "slf4j-simple" % slf4jVersion
)
    