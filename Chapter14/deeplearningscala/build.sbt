organization := "com.packtpub"

name := """deep-learning-scala"""

version := "0.0.2"

scalaVersion := "2.12.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


lazy val nd4jVersion="1.0.0-beta3"
lazy val dl4jVersion="1.0.0-beta3"

libraryDependencies ++= {
  val elastic4sV = "6.5.1"
  val scalaTestV = "3.0.5"
  val Log4jVersion = "2.11.1"
  Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sV,
    "com.sksamuel.elastic4s" %% "elastic4s-circe" % elastic4sV,
    // for the http client
    "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sV,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "org.apache.logging.log4j" % "log4j-api" % Log4jVersion,
    "org.apache.logging.log4j" % "log4j-core" % Log4jVersion,
    "org.apache.logging.log4j" % "log4j-1.2-api" % Log4jVersion,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    // deep learning libraries
    "com.fasterxml.jackson.core" % "jackson-core" % "2.9.8",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.8",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.9.8",
    "org.nd4j" % "nd4j-native-platform" % nd4jVersion,
  // ND4J backend. You need one in every DL4J project. Normally define artifactId as either "nd4j-native-platform" or "nd4j-cuda-9.2-platform" -->
    "org.nd4j" % "nd4j-native-platform" % nd4jVersion,
    "org.deeplearning4j" % "deeplearning4j-core" % dl4jVersion,
  // ParallelWrapper & ParallelInference live here
    "org.deeplearning4j" % "deeplearning4j-parallel-wrapper"% dl4jVersion
    //    "org.deeplearning4j" % "deeplearning4j-nlp" % dl4jVersion,
    //    "org.deeplearning4j" % "deeplearning4j-zoo" % dl4jVersion,
  )
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.jcenterRepo
)
