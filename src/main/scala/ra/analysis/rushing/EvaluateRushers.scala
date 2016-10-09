package ra.analysis.rushing

import ra.analysis.rushing.data.{AnalyzedRushRange, RushingDatum}
import ra.analysis.util.LoadUtils

object EvaluateRushers extends LoadUtils {

  def main(args: Array[String]) = {

    val rushingData: Seq[String] = getData("/ra/analysis/rushing/21st_century_rushers.csv")

    // Player,Team,Quarter,Time Left,Down,Yards To Go,Location,Score,Yards Rushed
    // Shaun Alexander,SEA,3,2:06,4,1,SEA 44,10-21,50
    // Edgerrin James,ARI,2,13:07,1,10,CRD 12,0-0,18

    val parsedData: Seq[RushingDatum] = RushingDatum.parseData(rushingData.drop(1))

    val averageRushByLocation: Seq[AnalyzedRushRange] =
      RushingDatum.
        findAverageRushByLocation(parsedData)

    val playerRushByLocation: Map[String, Seq[AnalyzedRushRange]] =
      parsedData.
        groupBy(_.playerName).
        mapValues(RushingDatum.findAverageRushByLocation)

    averageRushByLocation foreach println

    println

    playerRushByLocation foreach println

  }

}
