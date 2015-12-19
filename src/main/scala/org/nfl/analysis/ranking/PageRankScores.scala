package com.placeiq.pagerank

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.nfl.analysis.util.{LoadUtils, PageRankUtils}
import PageRankUtils._

object PageRankScores extends LoadUtils {

  type ScoreMap = Map[String, List[Double]]

  def main(args: Array[String]) {

    val gameLines = getData("/org/nfl/analysis/ranking/2015_game_scores")

    val sc = new SparkContext(new SparkConf()
      .setMaster("local[2]")
      .setAppName("PageRankScores")
      .set("spark.shuffle.consolidateFiles", "true")
      .set("spark.rdd.compress", "true")
      .set("spark.storage.memoryFraction", "0.6")
      .set("spark.shuffle.memoryFraction", "0.2")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryoserializer.buffer.mb", "10")
      .set("spark.kryo.referenceTracking", "false")
    )

    val teams = gameLines.flatMap { line => List(line.split(",")(0), line.split(",")(2)) }.distinct
    val vidToTeamName = teams.map(team => (vertexIdFromName(team), team))

    val namesRDD = VertexRDD[String](sc.parallelize(vidToTeamName))
    val gameOutcomesRDD = sc.parallelize(gameLines.map(generateGameEdges))

    val inputGraph = Graph.fromEdgeTuples(gameOutcomesRDD, 1.0)

    val iterationScoresOutput = PageRankUtils.generateIterationScoresMap(inputGraph, namesRDD, 0.4, 12)
    val convergenceScoresRDD = PageRankUtils.generateConvergenceRun(inputGraph, namesRDD, 0.001, 0.4)

    iterationScoresOutput foreach println
  }

}
