name := "Actoverse Demo"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.20"
)

lazy val root = project.in(file(".")).dependsOn(actoversePlugin)

lazy val actoversePlugin = RootProject(uri("https://github.com/45deg/Actoverse-Scala.git#v0.1.1"))
