package ra.analysis.ranking.pagerank.frame

import org.apache.spark.graphx.VertexId
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.graphframes.GraphFrame
import ra.analysis.ranking.pagerank.models.ScoreMap
import ra.analysis.ranking.pagerank.PageRankUtils

object PageRankFrameUtils extends PageRankUtils {

  case class PageRankOutput(teamName: String, pagerank: Double)
  case class GameOutcome(loser: VertexId, winner: VertexId)
  case class TeamName(id: VertexId, teamName: String)
  object TeamName {
    def apply(teamName: String): TeamName = TeamName(vertexIdFromName(teamName), teamName)
  }

  /**
    * Calculate the page rank scores for each vertex on each iteration.
    *
    * @param inputFrame -- input win-loss graph
    * @param teamNamesFrame -- mapping of vertex ID to team name
    * @param resetProb -- starting score on each vertex (non-personalized)
    * @param iters -- number of iterations run
    * @return a map pointing a team to a list of iteration scores
    */
  def generateIterationScoresMapFromFrame(
    spark: SparkSession,
    inputFrame: GraphFrame,
    teamNamesFrame: DataFrame,
    resetProb: Double,
    iters: Int
  ): ScoreMap = {
    import spark.implicits._

    var rankOutputGraph: GraphFrame = null
    var iterationScoresMap: ScoreMap = Map()
    teamNamesFrame.cache()

    for (i <- 1 until iters) {
      rankOutputGraph = inputFrame.pageRank.
        resetProbability(resetProb).
        maxIter(i).
        run()

      val iterationScoreMap = rankOutputGraph.vertices.
        select("teamName", "pagerank").
        as[PageRankOutput].
        collect()

      iterationScoresMap = iterationScoresMap ++ iterationScoreMap.map {
        case PageRankOutput(k, v) => k -> (iterationScoresMap.getOrElse(k, List()) :+ v)
      }

      rankOutputGraph.unpersist()
    }

    iterationScoresMap
  }


}
