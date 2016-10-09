package org.analysis.pythagorean

import java.text.DecimalFormat
import ra.analysis.util.LoadUtils

object WinDifferenceDistribution extends LoadUtils {

  /**
   * Round input double to a certain value.
   * @param input -- input number to round
   * @param digits -- number of significant digits to round to
   */
  def round(input: Double, digits: Int) : Double = {
    val df = new DecimalFormat("###." + ("0" * digits))
    df.format(input).toDouble
  }

  def roundToQuantile(input: Double, quantile: Int) : Double = {
    math.round(input * quantile) / quantile.toDouble
  }

  /**
   * Computes the average of a sequence of doubles.
   * @param input -- input sequence of doubles
   */
  def average(input: Seq[Double]) : Double = {
    input.foldLeft(0.0)(_ + _) / input.foldLeft(0.0)((r, c) => r + 1)
  }

  def main(args: Array[String]) = {

    // Gather pythogorean win input from resources (1988 through 2014).
    val inputLines = getData("/ra/analysis/pythagorean/wins_expwins_1988_2014.csv")

    // Format input file to grab relevant statistics.
    // Output: (team, year, actual wins, expected wins)
    val splitWinDiff = for (line <- inputLines) yield {
      val split = line.split(",", -1)
      val actualRecord = split(2).split('-')
      val expectedRecord = split(3).split('-')
      (split(0), split(1).toInt, actualRecord(0).toDouble, expectedRecord(0).toDouble)
    }

    // Group year sequences of win-tuples by team.
    // Output: (team, [(team, year, actual, expected)])
    val groupedWinDiffTuples = splitWinDiff.groupBy(_._1).map {
      case (team, years) => (team, years.toSeq)
    }.toSeq

    // For each team, zip the team's win history with a 1-year translation of
    // itself. Then construct a tuple with expectation difference and new win count.
    // Output: (wins this season, expectation difference last season)
    val combinedWinDiffTuples = groupedWinDiffTuples.flatMap { case (team, years) =>
      val zippedYears = years.drop(1) zip years
      zippedYears.map { case ((_, _, newWin, _), (_, _, oldWin, oldExp)) =>
        (newWin, round(oldExp - oldWin, 3))
      }
    }

    // Group by new record and gather all expected differences. Average and round
    // these differences to tie them back to new wins and then sort.
    // Output: (wins this season, previous-year-pythagorean-diff average)
    val recordKey = combinedWinDiffTuples.groupBy(_._1).map {
      case (record, groupedPrev) => (record, round(average(groupedPrev.map(_._2)), 2))
    }.toSeq.sortBy(_._1)
    recordKey.foreach(println)

    // Group by difference and gather all wins this season. Average and round
    // these differences to tie them back to new wins and then sort.
    // Output: (previous-year-pythagorean-diff, average wins this season)
    val differenceKey = combinedWinDiffTuples.groupBy(_._2).map {
      case (record, groupedPrev) => (record, round(average(groupedPrev.map(_._1)), 2))
    }.toSeq.sortBy(_._1)
    differenceKey.foreach(println)

  }

}
