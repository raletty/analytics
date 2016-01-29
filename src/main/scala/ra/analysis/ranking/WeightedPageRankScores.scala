package ra.analysis.ranking

import org.apache.spark.graphx.{Graph, VertexRDD}
import org.apache.spark.{SparkConf, SparkContext}
import ra.analysis.ranking.PageRankScores._
import ra.analysis.ranking.pagerank.PageRankUtils._

object WeightedPageRankScores {

  def main(args: Array[String]) {

    // Home teams had a record of 136-117 (away wins to weighted ~1.1623 higher).
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

    val teams = gameLines.flatMap { line => List(line.split(",")(4), line.split(",")(6))}.distinct
    val numWeeks = gameLines.map { line => line.split(",")(0)}.distinct.size

    val vidToTeamName = teams.map(team => (vertexIdFromName(team), team))
    val namesRDD = VertexRDD[String](sc.parallelize(vidToTeamName))

    val gameOutcomesRDD = sc.parallelize(gameLines.map(generateWeightedGameEdge))
    val gameGraph = Graph.fromEdges(gameOutcomesRDD, null)

  }

}
