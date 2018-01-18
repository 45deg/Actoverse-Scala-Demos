name := "Actoverse Demo"

version := "1.0"

scalaVersion := "2.11.8"

enablePlugins(ActoversePlugin)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.8"
)