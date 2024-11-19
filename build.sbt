name := """tubelytics-app-fall2024"""
organization := "com.app.fall2024.tubelytics"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += ehcache
libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.34.0",
  "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0",
  "com.google.http-client" % "google-http-client-jackson2" % "1.34.2"
)

testFrameworks += new TestFramework("org.junit.platform.suite.api.JUnitPlatform")
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.8.2" % Test

libraryDependencies += "org.mockito" % "mockito-core" % "5.2.0" % Test
libraryDependencies += "org.mockito" % "mockito-junit-jupiter" % "5.2.0" % Test

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

// Akka dependencies
val AkkaVersion = "2.6.20" // Latest compatible Akka version
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion // Akka Streams
)

libraryDependencies += "com.typesafe.play" %% "play-akka-http-server" % "2.8.20"
