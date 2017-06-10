import sbt._

object MyBuild extends Build {
  lazy val root = project.in(file(".")).dependsOn(githubRepo)
  lazy val githubRepo = uri("git://github.com/45deg/Actoverse-Scala.git")
}
