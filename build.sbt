import sbt.Keys._
import sbt._

name := """to-do-sample"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice

resolvers ++= Seq(
  "Nextbeat Releases"  at "http://maven.nextbeat.net.s3-ap-northeast-1.amazonaws.com/releases"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
  "net.ixias" %% "ixias"      % "1.1.36",
  "net.ixias" %% "ixias-aws"  % "1.1.36",
  "net.ixias" %% "ixias-play" % "1.1.36",
  "mysql"          % "mysql-connector-java" % "5.1.+",
  "ch.qos.logback" % "logback-classic"      % "1.1.+",
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
