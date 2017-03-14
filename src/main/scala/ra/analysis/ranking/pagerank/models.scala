package ra.analysis.ranking.pagerank

import org.apache.spark.graphx.VertexId
import ra.analysis.ranking.pagerank.gradient.GradientBuilder
import ra.analysis.util.LoadUtils.getData

object models {

  // Class for extracting relevant info from game scores.
  case class GameDescription(loser: VertexId, winner: VertexId, week: Int, away: Boolean, scoreDiff: Int)

  // Edge attribute in rank graph.
  case class GameEdgeAttribute(week: Int, weight: Double)

  // Gradients defining how certain weeks should be weighted in the ranking metric.
  type GameGradient = Map[Int, Double]
  type TeamGradient = Map[VertexId, GameGradient]

  // Shorthand type for iteration scores output.
  type ScoreMap = Map[String, List[Double]]



  trait GameOps[A] {
    def resourceFilename: String
    def gradientBuilder: GradientBuilder
    def generateGameDescription: String => GameDescription

    lazy val gameLines: Seq[String] = getData(resourceFilename)
  }

  case class GameOpsAux extends GameOps

  implicit object NFLOps extends GameOpsAux
}
