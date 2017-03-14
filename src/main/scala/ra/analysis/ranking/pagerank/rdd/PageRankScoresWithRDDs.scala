package ra.analysis.ranking.pagerank.rdd

import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.PageRank
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import ra.analysis.ranking.pagerank.models._
import ra.analysis.ranking.pagerank.rdd.PageRankRDDUtils._
import ra.analysis.util.LoadUtils.getData

object PageRankScoresWithRDDs {

  def main(args: Array[String]) {

    val gameLines = getData("/ra/analysis/ranking/full_2015_game_scores")

    val sc = new SparkContext(new SparkConf()
      .setMaster("local[*]")
      .setAppName("PageRankScores")
    )

    val teams = gameLines.flatMap { line => List(line.split(",")(4), line.split(",")(6)) }.distinct
    val vidToTeamName = teams.map(team => (vertexIdFromName(team), team))
    val teamNamesRDD: VertexRDD[String] = VertexRDD[String](sc.parallelize(vidToTeamName))

    val gameOutcomesRDD: RDD[(VertexId, VertexId)] = sc.parallelize(gameLines.map(generateGameEdge))
    val inputGraph: Graph[Double, PartitionID] = Graph.fromEdgeTuples(gameOutcomesRDD, 1.0)

    val iterationScoresOutput: ScoreMap = generateIterationScoresMap[PartitionID, Double](
      inputGraph = inputGraph,
      runIterations = PageRank.run(_, _, 0.4),
      teamNamesRDD = teamNamesRDD,
      iterations = 12
    )

    // val convergenceScoresRDD = generateConvergenceRun(inputGraph, teamNamesRDD, 0.001, 0.4)
    iterationScoresOutput foreach println

  }

}
