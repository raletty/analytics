package ra.analysis.ranking.pagerank

import org.apache.spark.graphx.VertexId
import ra.analysis.ranking.pagerank.gradient.{NbaGradientBuilder, NflGradientBuilder, GradientBuilder}
import ra.analysis.util.LoadUtils.getData

object models {

  sealed trait Sport
  class Nfl extends Sport
  class Nba extends Sport

  // Class for extracting relevant info from game scores.
  trait Describable[A <: Sport] {
    def loser: VertexId
    def winner: VertexId
    def gameNumber: Int
  }

  @deprecated case class GameDescription(loser: VertexId, winner: VertexId, week: Int, away: Boolean, scoreDiff: Int)

  case class NflGameDescription(
    loser: VertexId,
    winner: VertexId,
    gameNumber: Int,
    away: Boolean,
    scoreDiff: Int
  ) extends Describable[Nfl]

  case class NbaGameDescription(
    loser: VertexId,
    winner: VertexId,
    date: String,
    gameNumber: Int,
    away: Boolean,
    scoreDiff: Int
  ) extends Describable[Nba]

  // Edge attribute in rank graph.
  case class GameEdgeAttribute(week: Int, weight: Double)

  // Gradients defining how certain weeks should be weighted in the ranking metric.
  type GameGradient = Map[Int, Double]
  type TeamGradient = Map[VertexId, GameGradient]

  // Shorthand type for iteration scores output.
  type ScoreMap = Map[String, List[Double]]

  trait GameOps[A <: Sport] {
    def resourceFilename: String
    def gradientBuilder: GradientBuilder[A]
    def generateTeams: Seq[String] => Seq[String]
    def generateGameDescription: String => Describable[A]

    lazy val gameLines: Seq[String] = getData(resourceFilename)
    lazy val teams: Seq[String] = generateTeams(gameLines)
  }

  case class GameOpsAux[A <: Sport](
    resourceFilename: String,
    gradientBuilder: GradientBuilder[A],
    generateTeams: Seq[String] => Seq[String],
    generateGameDescription: String => Describable[A]
  ) extends GameOps[A]

  implicit object NFLOps extends GameOpsAux[Nfl](
    resourceFilename = "/ra/analysis/ranking/full_2015_game_scores",
    gradientBuilder = new NflGradientBuilder,
    generateTeams = PageRankUtils.generateNflTeams,
    generateGameDescription = PageRankUtils.generateNflGameDescription
  )

  implicit object NBAOps extends GameOpsAux[Nba](
    resourceFilename = "/ra/analysis/ranking/full_2017_nba_game_scores",
    gradientBuilder = new NbaGradientBuilder,
    generateTeams = PageRankUtils.generateNbaTeams,
    generateGameDescription = PageRankUtils.generateNbaGameDescription
  )
}
