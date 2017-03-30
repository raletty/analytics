package ra.analysis.rushing

import ra.analysis.rushing.data.{RushRange, NormalizedRushRange, AnalyzedRushRange, RushingDatum}
import ra.analysis.util.LoadUtils.getData
import scala.collection.breakOut

object EvaluateRushers {

  def main(args: Array[String]) = {

    val rushingData: Seq[String] = getData("/ra/analysis/rushing/top_25_rushers_2016")

    /**
     * Player,Team,Quarter,Time Left,Down,Yards To Go,Location,Score,Yards Rushed
     * Shaun Alexander,SEA,3,2:06,4,1,SEA 44,10-21,50
     * Edgerrin James,ARI,2,13:07,1,10,CRD 12,0-0,18
     **/

    val parsedData: Seq[RushingDatum] = RushingDatum.parseData(rushingData.drop(1))
    val numRushers: Int               = parsedData.map(_.playerName).distinct.size
    val yardageBuckets: Int           = 10

    val averageRushRanges: Seq[AnalyzedRushRange] =
      RushingDatum.findAverageRushByLocation(yardageBuckets)(parsedData)

    val playerRushByLocation: Map[String, Seq[AnalyzedRushRange]] =
      parsedData.
        groupBy { _.playerName }.
        mapValues { RushingDatum.findAverageRushByLocation(yardageBuckets) }

    val normalizedPlayerRanges: Seq[(String, Seq[NormalizedRushRange])] =
      playerRushByLocation.
        map { case (playerName, playerRushRanges) =>
          val toNormalizedRushRange = (AnalyzedRushRange.produceComparisonToAverage(numRushers, 1.0) _).tupled
          val normalizedRanges = (playerRushRanges zip averageRushRanges).map { toNormalizedRushRange }
          (playerName, normalizedRanges)
        } (breakOut)

    println("Normalized Ranges: ")
    formatScores(normalizedPlayerRanges).foreach(println)
    println("Pure Ranges: ")
    formatScores(playerRushByLocation.toSeq).foreach(println)

    // TODO: Output JS usable metric/percentage values. Build TD graphic as a wrapper around yardage graphic (for signature)?

  }

  def formatScores(normalizedPlayerRanges: Seq[(String, Seq[RushRange])]): Seq[String] = {
    for {
      (name, ranges) <- normalizedPlayerRanges
      range <- ranges
    } yield s"$name,${range.csvString}"
  }

}
