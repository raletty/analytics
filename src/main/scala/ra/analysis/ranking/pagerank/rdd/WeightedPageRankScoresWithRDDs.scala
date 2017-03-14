package ra.analysis.ranking.pagerank.rdd

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.{Graph, VertexRDD}
import org.apache.spark.{SparkConf, SparkContext}
import ra.analysis.ranking.pagerank.models
import models._
import ra.analysis.ranking.pagerank.rdd.PageRankRDDUtils._
import ra.analysis.ranking.pagerank.gradient.NFLGradientBuilder
import ra.analysis.util.LoadUtils._

object WeightedPageRankScoresWithRDDs {

  def runWeightedPageRankWithRDDs[A : GameOps](
    resetProb: Double,
    numIters: Int
  ): ScoreMap = {
    val gameLines = getData("/ra/analysis/ranking/full_2015_game_scores")

    val sc = new SparkContext(
      new SparkConf()
        .setMaster("local[*]")
        .setAppName("WeightedPageRankScoresWithRDDs")
    )

//    val gameOps = implicitly[GameOps[A]]

    val teams = gameLines.flatMap { line => List(line.split(",")(4), line.split(",")(6)) }.distinct
    val vidToTeamName = teams.map(team => (vertexIdFromName(team), team))
    val teamNamesRDD = VertexRDD[String](sc.parallelize(vidToTeamName))

    val gameDescriptions = gameLines.map(generateGameDescription)
    val teamGradients: TeamGradient = gameDescriptions.
      groupBy(_.loser).
      mapValues { buildTeamGradientEntry(new NFLGradientBuilder) }.
      map(identity)

    val gameEdgeRDD = sc.parallelize(gameDescriptions.map(generateWeightedGameEdge))
    val gameGraph = Graph.fromEdges(gameEdgeRDD, 1.0)

    val teamGradientBroadcast: Broadcast[TeamGradient] = sc.broadcast(teamGradients)
    val weightedIterationOutput = generateIterationScoresMap[GameEdgeAttribute, GameEdgeAttribute](
      inputGraph = gameGraph,
      runIterations = EdgeWeightedPageRank.run[Double](teamGradientBroadcast, 0.4),
      teamNamesRDD = teamNamesRDD,
      iterations = 12
    )

    weightedIterationOutput
  }

}
