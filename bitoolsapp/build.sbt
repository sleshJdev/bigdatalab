val ScalatraVersion = "2.6.5"
val SlickVersion = "3.3.0"
val DropwizardVersion = "4.0.5"

organization := "com.itechart"

name := "BiToolsApp"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

  "com.typesafe.slick" %% "slick" % SlickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
  "mysql" % "mysql-connector-java" % "6.0.6",

  "nl.grons" %% "metrics4-scala" % "4.0.4",
  "io.dropwizard.metrics" % "metrics-core" % DropwizardVersion,
  "io.dropwizard.metrics" % "metrics-graphite" % DropwizardVersion
)

javaOptions ++= Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044")

containerPort in Jetty := 9889

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
