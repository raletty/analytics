package ra.analysis.ranking

import ra.analysis.ranking.pagerank.PageRankUtils._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import ra.analysis.util.LoadUtils

object PageRankScores extends LoadUtils {

  def main(args: Array[String]) {

    val gameLines = getData("/ra/analysis/ranking/full_2015_game_scores")

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

    val teams = gameLines.flatMap { line => List(line.split(",")(4), line.split(",")(6)) }.distinct
    val vidToTeamName = teams.map(team => (vertexIdFromName(team), team))

    val namesRDD = VertexRDD[String](sc.parallelize(vidToTeamName))
    val gameOutcomesRDD = sc.parallelize(gameLines.map(generateGameEdge))

    val inputGraph = Graph.fromEdgeTuples(gameOutcomesRDD, 1.0)

    val iterationScoresOutput = generateIterationScoresMap(inputGraph, namesRDD, 0.4, 12)
    val convergenceScoresRDD = generateConvergenceRun(inputGraph, namesRDD, 0.001, 0.4)

    iterationScoresOutput foreach println
  }

}
