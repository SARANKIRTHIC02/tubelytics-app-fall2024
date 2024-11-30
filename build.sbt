name := """tubelytics-app-fall2024"""
organization := "com.app.fall2024.tubelytics"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-streams" % play.core.PlayVersion.current,
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.20",
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",
  "com.typesafe.play" %% "play-test" % play.core.PlayVersion.current % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.6.20" % Test
)

libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.34.0",
  "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0",
  "com.google.http-client" % "google-http-client-jackson2" % "1.34.2"
)
testFrameworks += new TestFramework("org.junit.platform.suite.api.JUnitPlatform")
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.8.2" % Test

libraryDependencies += "org.mockito" % "mockito-core" % "5.2.0" % Test
libraryDependencies += "org.mockito" % "mockito-junit-jupiter" % "5.2.0" % Test

lazy val akkaVersion = "2.6.21"
dependencyOverrides ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)