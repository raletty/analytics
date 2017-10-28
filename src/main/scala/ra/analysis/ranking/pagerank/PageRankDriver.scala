package ra.analysis.ranking.pagerank

import ra.analysis.ranking.pagerank.models.{ ScoreMap, NFL, NBA }
import ra.analysis.ranking.pagerank.rdd.{ WeightedPageRankScoresWithRDDs, PageRankScoresWithRDDs }

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.concurrent.blocking
import scala.concurrent.ExecutionContext.Implicits.global

object PageRankDriver {

  def main( args: Array[String] ) = {

    //    val normalScoreMap: ScoreMap = PageRankScoresWithRDDs.runPageRankWithRDDs[NFL](0.4, 10)
    //    val weightedScoreMap: ScoreMap = WeightedPageRankScoresWithRDDs.runWeightedPageRankWithRDDs[NFL](0.4, 10)
    //    val normalScoreMap: ScoreMap = PageRankScoresWithRDDs.runPageRankWithRDDs[NBA](0.4, 10)
    val weightedScoreMap: ScoreMap = WeightedPageRankScoresWithRDDs.runWeightedPageRankWithRDDs[NBA]( 0.4, 10 )

    //    println("NORMAL SCORE OUTPUT\n================")
    //    normalScoreMap.toSeq.sortBy { _._2.last } foreach println
    println( "WEIGHTED SCORE OUTPUT\n==================" )
    weightedScoreMap.toSeq.sortBy { _._2.last } foreach println

  }

}
