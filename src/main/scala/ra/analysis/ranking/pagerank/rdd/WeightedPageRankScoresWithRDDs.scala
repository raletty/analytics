package ra.analysis.ranking.pagerank.rdd

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.{ Graph, VertexRDD }
import org.apache.spark.{ SparkConf, SparkContext }
import ra.analysis.ranking.pagerank.models
import models._
import ra.analysis.ranking.pagerank.rdd.PageRankRDDUtils._

object WeightedPageRankScoresWithRDDs {

  def runWeightedPageRankWithRDDs[A <: Sport: GameOps](
    resetProb: Double,
    numIters: Int
  ): ScoreMap = {

    val gameOps = implicitly[GameOps[A]]
    val gameLines = gameOps.gameLines.tail
    val teams = gameOps.teams

    val sc = new SparkContext(
      new SparkConf()
        .setMaster("local[*]")
        .setAppName("WeightedPageRankScoresWithRDDs")
    )

    val vidToTeamName = teams.map(team => (vertexIdFromName(team), team))
    val teamNamesRDD = VertexRDD[String](sc.parallelize(vidToTeamName))

    val gameDescriptions: Seq[Describable[A]] = gameLines.
      map(gameOps.generateGameDescription).
      collect { case Some(desc) => desc }

    val teamGradients: TeamGradient = gameDescriptions.
      groupBy(_.loser).
      mapValues { buildTeamGradientEntry[A](gameOps.gradientBuilder) }.
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
