
name := "manmosu"

version := "0.0.2"

scalaVersion := "2.13.4"

routesGenerator := InjectedRoutesGenerator

maintainer := "your.name@company.org"

resolvers ++= Seq(
    "Typesafe repository releases" at "https://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",  
    "Atlassian Releases" at "https://maven.atlassian.com/public/", 
    "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
  )
  
libraryDependencies ++= Seq(ws, filters, guice)
libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.11",  // needed for play-silhouette to work on Java 10
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test",
  "com.iheart" %% "ficus" % "1.5.0",
  "com.typesafe.play" %% "play" % "2.8.7",
  "net.codingwell" %% "scala-guice" % "4.2.11",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "org.apache.commons" % "commons-email" % "1.5",
  "javax.xml.bind" % "jaxb-api" % "2.3.1",
  "junit" % "junit" % "4.13.1" % "test",
  "mysql" % "mysql-connector-java" % "8.0.22",
  "org.jsoup" % "jsoup" % "1.13.1",
  "org.scalatest" %% "scalatest" % "3.2.3" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
  "org.webjars" %% "webjars-play" % "2.8.0",
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",
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
  // "-Xmax-classfile-name", "100", // 2.12 and lower only
  // "-Yno-adapted-args",       // 2.12 and lower only
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",   
  "-Ywarn-value-discard"
 //  "-Ywarn-unused-import"     // 2.11 only
)

