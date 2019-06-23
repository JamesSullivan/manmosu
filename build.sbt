
name := "manmosu"

version := "0.0.1"

scalaVersion := "2.12.8"

routesGenerator := InjectedRoutesGenerator

maintainer := "your.name@company.org"

resolvers ++= Seq(
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",  
    "Atlassian Releases" at "https://maven.atlassian.com/public/", 
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  )
  
libraryDependencies ++= Seq(ws, filters, guice)
libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.9",  // needed for play-silhouette to work on Java 10
  "com.mohiva" %% "play-silhouette" % "6.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "6.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "6.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "6.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "6.0.0" % "test",
  "com.iheart" %% "ficus" % "1.4.7",
  "com.typesafe.play" % "play_2.12" % "2.7.3",
  "net.codingwell" %% "scala-guice" % "4.2.5",
  "com.typesafe.play" % "play-slick_2.12" % "4.0.2",
  "org.apache.commons" % "commons-email" % "1.5",
  "javax.xml.bind" % "jaxb-api" % "2.3.1",
  "junit" % "junit" % "4.12" % "test",
  "mysql" % "mysql-connector-java" % "8.0.16",
  "org.jsoup" % "jsoup" % "1.12.1",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test",
  "org.webjars" %% "webjars-play" % "2.7.3",
  "com.typesafe.play" %% "play-mailer" % "7.0.0",
  "com.typesafe.play" %% "play-mailer-guice" % "7.0.0",
  "es.nitaur.markdown" % "txtmark" % "0.16",
  ehcache
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

import com.typesafe.sbt.packager.MappingsHelper._
    mappings in Universal ++= directory(baseDirectory.value / "public")
       
scalacOptions ++= Seq(
  // "-target:jvm-1.8",
  "-deprecation",           
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",                
  "-unchecked",
  // "-Xfatal-warnings",       
  // "-Xlint",
  "-Xmax-classfile-name", "100",
  "-Yno-adapted-args",       
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",   
  "-Ywarn-value-discard",
  "-Xfuture"
 //  "-Ywarn-unused-import"     // 2.11 only
)


