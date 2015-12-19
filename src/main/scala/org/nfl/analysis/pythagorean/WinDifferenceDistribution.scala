package org.nfl.analysis.pythagorean

import java.text.DecimalFormat

import org.nfl.analysis.util.LoadUtils

object WinDifferenceDistribution extends LoadUtils {

  def round(input: Double) : Double = {
    val df = new DecimalFormat("###.##")
    df.format(input).toDouble
  }

  def average(input: List[Double]) : Double = {
    input.foldLeft(0.0)(_ + _) / input.foldLeft(0.0)((r, c) => r + 1)
  }

  def main(args: Array[String]) = {

    val inputLines = getData("/org/nfl/analysis/wins_expwins_1988_2014.csv")

    // Format input file to grab relevant statistics.
    // Output: (team, year, actual wins, expected wins)
    val splitWinDiff = for (line <- inputLines) yield {
      val split = line.split(",")
      val actualRecord = split(2).split('-')
      val expectedRecord = split(3).split('-')
      (split(0), split(1).toInt, actualRecord(0).toDouble, expectedRecord(0).toDouble)
    }

    // Group year sequences of win-tuples by team.
    // Output: (team, [(team, year, actual, expected)])
    val groupedWinDiffTuples = splitWinDiff.groupBy(_._1).map {
      case (team: String, years: Seq[(String, Int, Double, Double)]) => (team, years.toList)
    }.toList

    // 
    //
    val combinedWinDiffTuples = for (teamYears <- groupedWinDiffTuples) yield {
      val years = teamYears._2
      val zippedYears = years.drop(1) zip years
      zippedYears.map {
        case ((_, _, newWin, _), (_, _, oldWin, oldExp)) => (round(oldExp - oldWin), newWin)
      }
    }

    val recordKey = combinedWinDiffTuples.flatten.groupBy(_._2).map {
      case (record: Double, tuples: List[(Double, Double)]) =>
        val prevExpWins = for (tuple <- tuples) yield tuple._1
        (record, round(average(prevExpWins)))
    }.toList.sortBy(_._1)
    recordKey.foreach(println)

//    combinedWinDiffTuples.flatten.sortBy(_._1).foreach(println)

    val differenceKey = combinedWinDiffTuples.flatten.groupBy(_._1).map {
      case (record: Double, tuples: List[(Double, Double)]) =>
        val prevExpWins = for (tuple <- tuples) yield tuple._2
        (record, round(average(prevExpWins)))
    }.toList.sortBy(_._1)
    differenceKey.foreach(println)

  }

}
