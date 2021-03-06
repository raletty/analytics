import sbt._
import Keys._
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

lazy val commonSettings = Seq(
  organization := "ra.analysis",
  version := "0.0.1",
  scalaVersion := "2.12.8"
)

lazy val sparkVersion       = "2.4.5"
lazy val scalazVersion      = "7.2.0"
lazy val catsVersion        = "2.1.0"
lazy val scalaArmVersion    = "2.0"
lazy val json4sVersion      = "3.6.7"
//lazy val graphFramesVersion = "0.4.0-SNAPSHOT-spark2.1"

lazy val scalacticVersion   = "3.0.4"
lazy val scalatestVersion   = "3.0.4"

lazy val sparkCore          = "org.apache.spark" %% "spark-core" % sparkVersion withSources() withJavadoc()
lazy val sparkSQL           = "org.apache.spark" %% "spark-sql" % sparkVersion withSources() withJavadoc()
lazy val sparkGraphX        = "org.apache.spark" %% "spark-graphx" % sparkVersion withSources() withJavadoc()
//lazy val sparkGraphFrames   = "default" %% "graphframes" % graphFramesVersion withSources() withJavadoc()

lazy val scalaz             = "org.scalaz" %% "scalaz-core" % scalazVersion withSources() withJavadoc()
lazy val cats               = "org.typelevel" %% "cats-core" % catsVersion withSources() withJavadoc()
lazy val scalaArm           = "com.jsuereth" %% "scala-arm" % scalaArmVersion withSources() withJavadoc()
lazy val json4sNative       = "org.json4s" %% "json4s-native" % json4sVersion withSources() withJavadoc()

lazy val scalatest          = "org.scalatest" %% "scalatest" % scalatestVersion % "test"
lazy val scalactic          = "org.scalactic" %% "scalactic" % scalacticVersion

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(commonScalariformSettings: _*).
  settings(
    name := "Sports Analysis",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies += sparkCore,
    libraryDependencies += sparkSQL,
    libraryDependencies += sparkGraphX,
//    libraryDependencies += sparkGraphFrames,
    libraryDependencies += cats,
    libraryDependencies += scalaArm,
    libraryDependencies += scalatest,
    libraryDependencies += scalactic,
    scalaSource in Compile := baseDirectory.value / "src/main/scala/",
    scalaSource in Test := baseDirectory.value / "src/test/scala/",
    resourceDirectory in Compile := baseDirectory.value / "src/main/resources/",
    maxErrors := 20,
    initialCommands in console := "import ra.analysis.distance._, ra.analysis.ranking.pagerank._, ra.analysis.distance._",
    fork := true
  )

lazy val commonScalariformSettings =
  addCommandAlias("format", ";test:scalariformFormat ;scalariformFormat") ++ (
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignParameters, true)
      .setPreference(AlignArguments, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
      .setPreference(SpaceInsideParentheses, true)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(SpacesAroundMultiImports, true)
      .setPreference(DoubleIndentConstructorArguments, false)
  )