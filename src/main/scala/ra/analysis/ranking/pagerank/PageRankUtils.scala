package ra.analysis.ranking.pagerank

import org.apache.spark.graphx._
import ra.analysis.ranking.pagerank.models._
import ra.analysis.ranking.pagerank.gradient.GradientBuilder

trait PageRankUtils {

  // Given input game line, creates an NFL game description.
  def generateNflGameDescription(gameLine: String): Option[Describable[NFL]] = {
    val split = gameLine.split(",")
    Some(
      NflGameDescription(
        loser = vertexIdFromName(split(6)),
        winner = vertexIdFromName(split(4)),
        gameNumber = split(0).toInt,
        away = split(5).nonEmpty,
        scoreDiff = split(7).toInt - split(8).toInt
      )
    )
  }

  def generateNbaGameDescription(gameLine: String): Option[Describable[NBA]] = {
    val split = gameLine.split(",")

    NbaGameDescription(
      loser = vertexIdFromName(split(4)),
      winner = vertexIdFromName(split(0)),
      date = split(2),
      gameNumber = split(1).toInt,
      away = split(3).nonEmpty,
      scoreDiff = split(6).toInt - split(7).toInt
    ).optionOn(split(5) == "W")
  }

  // Given some game lines, generate all teams for that sport.
  def generateNflTeams(gameLines: Seq[String]): Seq[String] = {
    gameLines.flatMap { line =>
      val split = line.split(",")
      List(split(4), split(6))
    }.distinct
  }

  def generateNbaTeams(gameLines: Seq[String]): Seq[String] = {
    gameLines.flatMap { line =>
      val split = line.split(",")
      List(split(0), split(4))
    }.distinct
  }

  // Given a game description, creates a weighted game edge.
  def generateWeightedGameEdge[A <: Sport](gameDesc: Describable[A]): Edge[GameEdgeAttribute] = {
    val edgeAttr = GameEdgeAttribute(gameDesc.gameNumber, 1.0)
    Edge(gameDesc.loser, gameDesc.winner, edgeAttr)
  }

  // Applies a gradient builder to a set of descriptions to produce a gradient.
  def buildTeamGradientEntry[A <: Sport](
    builder: GradientBuilder[A]
  )(
    descriptions: Seq[Describable[A]]
  ): GameGradient = {
    descriptions.map {
      case NflGameDescription(_, _, gameNumber, away, difference) =>
        (gameNumber, builder.evaluateGameWeight(gameNumber, away, difference))
      case NbaGameDescription(_, _, _, gameNumber, away, difference) =>
        (gameNumber, builder.evaluateGameWeight(gameNumber, away, difference))
    }.toMap
  }

  // Used to transform team names into vertex ID's.
  def vertexIdFromName(name: String): VertexId = name.replaceAll("\\s", "").toLowerCase.hashCode

}

object PageRankUtils extends PageRankUtils