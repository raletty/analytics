import sbt._
import Keys._

lazy val commonSettings = Seq(
  organization := "org.nfl",
  version := "0.0.1",
  scalaVersion := "2.10.4"
)

lazy val sparkVersion = "1.5.2"
lazy val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion withSources() withJavadoc()
lazy val sparkSQL = "org.apache.spark" %% "spark-sql" % sparkVersion withSources() withJavadoc()
lazy val sparkGraphX = "org.apache.spark" %% "spark-graphx" % sparkVersion withSources() withJavadoc()

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "NFL Analysis",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies += sparkCore,
    libraryDependencies += sparkSQL,
    libraryDependencies += sparkGraphX,
    scalaSource in Compile := baseDirectory.value / "src/main/scala/",
    scalaSource in Test := baseDirectory.value / "src/test/scala/",
    resourceDirectory in Compile := baseDirectory.value / "src/main/resources/",
    maxErrors := 20,
    pollInterval := 1000,
    initialCommands in console := "import org.nfl.analysis.distance._, org.nfl.analysis.pagerank._, org.nfl.analysis.org.nfl.analysis.distance._",
    fork := true
  )
