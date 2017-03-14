package ra.analysis.ranking.pagerank.rdd

import org.apache.spark.graphx.lib.PageRank
import org.apache.spark.graphx.{VertexRDD, Graph}
import org.apache.spark.rdd.RDD
import ra.analysis.ranking.pagerank.models.ScoreMap
import ra.analysis.ranking.pagerank.PageRankUtils

object PageRankRDDUtils extends PageRankUtils {

  /**
    * Calculate the page rank scores for each vertex on each iteration.
    *
    * @param inputGraph -- input win-loss graph
    * @param teamNamesRDD -- mapping of vertex ID to team name
    * @param iterations -- number of iterations run
    * @return a map pointing a team to a list of iteration scores
    */
  def generateIterationScoresMap[ED1, ED2](
    inputGraph: Graph[Double, ED1],
    runIterations: (Graph[Double, ED1], Int) => Graph[Double, ED2],
    teamNamesRDD: VertexRDD[String],
    iterations: Int
  ): ScoreMap = {
    var rankOutputGraph: Graph[Double, ED2] = null
    var iterationScoresMap: ScoreMap = Map()
    teamNamesRDD.cache()

    for (i <- 1 until iterations) {
      rankOutputGraph = runIterations(inputGraph, i)

      val iterationScoreMap = rankOutputGraph.
        outerJoinVertices(teamNamesRDD) { case (vid, score, Some(name)) => (name, score) }.
        vertices.map { case (_, weight) => weight }.
        collectAsMap()

      iterationScoresMap = iterationScoresMap ++ iterationScoreMap.map {
        case (k, v) => k -> (iterationScoresMap.getOrElse(k, List()) :+ v)
      }

      rankOutputGraph.unpersist()
    }

    iterationScoresMap
  }

  /**
    * An alternative to the above where iterations are run until the scores converge with some tolerance.
    *
    * @param inputGraph -- input win-loss graph
    * @param teamNamesRDD -- mapping of vertex ID to team name
    * @param tolerence -- epsilon within which final interation difference should lie
    * @param resetProb -- starting score on each vertex (non-personalized)
    * @return a map pointing a team to a list of iteration scores
    */
  def generateConvergenceRun(
    inputGraph: Graph[Double, Int],
    teamNamesRDD: VertexRDD[String],
    tolerence: Double = 0.001,
    resetProb: Double = 0.4
  ): RDD[(String, Double)] = {
    val rankOutputGraph = PageRank.runUntilConvergence(inputGraph, tolerence, resetProb)
    val namedOutputGraph = rankOutputGraph.outerJoinVertices(teamNamesRDD) {
      case (_, score, Some(name)) => (name, score)
    }

    namedOutputGraph.vertices.map(_._2).coalesce(1).sortBy(_._2, ascending = false)
  }

}
