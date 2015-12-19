package org.nfl.analysis.util

import com.placeiq.pagerank.PageRankScores.ScoreMap
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._
import org.apache.spark.rdd.RDD

object PageRankUtils {

  def generateGameEdges(gameLine: String): (VertexId, VertexId) = {
    val split = gameLine.split(",")
    (vertexIdFromName(split(2)), vertexIdFromName(split(0)))
  }

  def vertexIdFromName(name: String): VertexId = {
    name.replaceAll("\\s", "").toLowerCase.hashCode
  }

  def generateIterationScoresMap(
        inputGraph: Graph[Double, Int],
        teamNamesRDD: VertexRDD[String],
        resetProb: Double,
        iters: Int
  ): ScoreMap = {

    var rankOutputGraph: Graph[Double, Double] = null
    var iterationScoresMap: ScoreMap = Map()
    teamNamesRDD.cache()

    for (i <- 0 until iters) {
      rankOutputGraph = PageRank.run(inputGraph, i, resetProb)
      val iterationScoreMap = rankOutputGraph.outerJoinVertices(teamNamesRDD) {
        case (vid, score, Some(name)) => (name, score)
      }.vertices.map(_._2).collectAsMap()

      iterationScoresMap = iterationScoresMap ++ iterationScoreMap.map {
        case (k, v) => k -> (iterationScoresMap.getOrElse(k, List()) :+ v)
      }
    }

    iterationScoresMap
  }

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
