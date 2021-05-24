package ra.analysis.ranking.pagerank.rdd

import org.apache.spark.graphx._
import org.apache.spark.graphx.lib.PageRank
import org.apache.spark.rdd.RDD
import org.apache.spark.{ SparkConf, SparkContext }

import ra.analysis.ranking.pagerank.models._
import ra.analysis.ranking.pagerank.rdd.PageRankRDDUtils._

object PageRankScoresWithRDDs {

  def runPageRankWithRDDs[A <: Sport: GameOps](
    resetProb: Double,
    numIters:  Int ): ScoreMap = {

    val gameOps = implicitly[GameOps[A]]
    val gameLines = gameOps.gameLines.tail
    val teams = gameOps.teams

    val sc = new SparkContext( new SparkConf()
      .setMaster( "local[*]" )
      .setAppName( "PageRankScores" ) )

    val vidToTeamName: Seq[( VertexId, String )] = teams.map( team => ( vertexIdFromName( team ), team ) )
    val teamNamesRDD: VertexRDD[String] = VertexRDD[String]( sc.parallelize( vidToTeamName ) )

    val gameDescriptions: Seq[Describable[A]] = gameLines.
      map( gameOps.generateGameDescription ).
      collect { case Some( desc ) => desc }

    val gameEdges: Seq[( VertexId, VertexId )] = gameDescriptions.
      map { desc => ( desc.loser, desc.winner ) }

    val gameOutcomesRDD: RDD[( VertexId, VertexId )] = sc.parallelize( gameEdges )
    val inputGraph: Graph[Double, PartitionID] = Graph.fromEdgeTuples( gameOutcomesRDD, 1.0 )

    val iterationScoresOutput: ScoreMap = generateIterationScoresMap[PartitionID, Double](
      inputGraph    = inputGraph,
      runIterations = PageRank.run( _, _, 0.4 ),
      teamNamesRDD  = teamNamesRDD,
      iterations    = 12 )

    iterationScoresOutput
  }

}
