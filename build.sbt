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

//libraryDependencies += "org.mockito" % "mockito-core" % "5.2.0" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.16.42" % Test
libraryDependencies += "org.mockito" % "mockito-junit-jupiter" % "5.2.0" % Test
