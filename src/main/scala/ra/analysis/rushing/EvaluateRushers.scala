package ra.analysis.rushing

import ra.analysis.rushing.data.{NormalizedRushRange, AnalyzedRushRange, RushingDatum}
import ra.analysis.util.LoadUtils
import scala.collection.breakOut

object EvaluateRushers extends LoadUtils {

  def main(args: Array[String]) = {

    val rushingData: Seq[String] = getData("/ra/analysis/rushing/21st_century_rushers.csv")

    // Player,Team,Quarter,Time Left,Down,Yards To Go,Location,Score,Yards Rushed
    // Shaun Alexander,SEA,3,2:06,4,1,SEA 44,10-21,50
    // Edgerrin James,ARI,2,13:07,1,10,CRD 12,0-0,18

    val parsedData: Seq[RushingDatum] = RushingDatum.parseData(rushingData.drop(1))

    val averageRushRanges: Seq[AnalyzedRushRange] =
      RushingDatum.
        findAverageRushByLocation(parsedData)

    val playerRushByLocation: Map[String, Seq[AnalyzedRushRange]] =
      parsedData.
        groupBy { _.playerName }.
        mapValues { RushingDatum.findAverageRushByLocation }

    val normalizedPlayerRanges: Seq[(String, Seq[NormalizedRushRange])] =
      playerRushByLocation.
        map { case (playerName, playerRushRanges) =>
          val toNormalizedRushRange = (AnalyzedRushRange.produceComparisonToAverage(32, 1.0) _).tupled
          val normalizedRanges = (playerRushRanges zip averageRushRanges).map { toNormalizedRushRange }
          (playerName, normalizedRanges)
        } (breakOut)

    println(averageRushRanges)
    println
    playerRushByLocation.foreach(println)
    println
    normalizedPlayerRanges.foreach(println)

  }

}
