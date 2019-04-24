name := """restapi"""

version := "1.0-SNAPSHOT"

maintainer := "yauheni.putsykovich@itechart.com"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)

javaOptions ++= Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044")
