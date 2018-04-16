
name := "manmosu"

version := "0.0.1"

scalaVersion := "2.12.4"

routesGenerator := InjectedRoutesGenerator


resolvers ++= Seq(
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",  
    "Atlassian Releases" at "https://maven.atlassian.com/public/", 
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  )
  
libraryDependencies ++= Seq(ws, filters, guice)
libraryDependencies ++= Seq(

  "com.mohiva" %% "play-silhouette" % "5.0.4",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.4",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.4",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.4",
  "com.mohiva" %% "play-silhouette-testkit" % "5.0.4" % "test",
  "com.iheart" %% "ficus" % "1.4.3",
  "com.typesafe.play" % "play_2.12" % "2.6.13",
  "net.codingwell" %% "scala-guice" % "4.2.0",
  "com.typesafe.play" % "play-slick_2.12" % "3.0.3",
  "org.apache.commons" % "commons-email" % "1.5",
  "javax.xml.bind" % "jaxb-api" % "2.3.0",
  "junit" % "junit" % "4.12" % "test",
  "mysql" % "mysql-connector-java" % "8.0.8-dmr",
  "org.jsoup" % "jsoup" % "1.11.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test",
  "org.webjars" %% "webjars-play" % "2.6.3",
  // "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B4-SNAPSHOT",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
  "es.nitaur.markdown" % "txtmark" % "0.16",
  ehcache
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

import com.typesafe.sbt.packager.MappingsHelper._
    mappings in Universal ++= directory(baseDirectory.value / "public")
       
scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-deprecation",           
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",                
  "-unchecked",
  //"-Xfatal-warnings",       
  // "-Xlint",
  "-Xmax-classfile-name", "100",
  "-Yno-adapted-args",       
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",   
  "-Ywarn-value-discard",
  "-Xfuture"
 //  "-Ywarn-unused-import"     // 2.11 only
)


