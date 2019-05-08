organization := "com.packtpub"

name := """elastic4s-sample"""

version := "0.0.2"

scalaVersion := "2.12.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val elastic4sV = "6.5.0"
  val scalaTestV = "3.0.5"
  val Log4jVersion = "2.11.1"
  Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sV,
    "com.sksamuel.elastic4s" %% "elastic4s-circe" % elastic4sV,
    // for the http client
    "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sV,
    // if you want to use reactive streams
    "com.sksamuel.elastic4s" %% "elastic4s-http-streams" % elastic4sV,
    // testing
    "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sV % "test",
    "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sV % "test",
    "org.apache.logging.log4j" % "log4j-api" % Log4jVersion,
    "org.apache.logging.log4j" % "log4j-core" % Log4jVersion,
    "org.apache.logging.log4j" % "log4j-1.2-api" % Log4jVersion,
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  )
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.jcenterRepo
)
