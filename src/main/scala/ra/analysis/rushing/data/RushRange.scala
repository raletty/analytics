package ra.analysis.rushing.data

case class AnalyzedRushRange(
  range: YardRange,
  averageRush: Double,
  numRushes: Int,
  numTouchdowns: Int
) {
  override def toString: String =
    s"{range: ${range.rangeString}, averageRush: $averageRush, rushes: $numRushes, touchdowns: $numTouchdowns}"
}

case class NormalizedRushRange(
  range: YardRange,
  normalizedAvgRush: Double,
  normalizedNumRushes: Double,
  normalizedTdRate: Double,
  normalizedNumTds: Double
) {
  override def toString: String =
    s"{range: ${range.rangeString}, nAverageRush: $normalizedAvgRush, nRushes: $normalizedNumRushes, nTouchdownRate: $normalizedTdRate, nTouchdowns: $normalizedNumTds}"
}

object AnalyzedRushRange {

  def normalizeMetricToAverage(
    playerStat: Double,
    avgStat: Double,
    offset: Double
  ): Double = {
    offset + (playerStat - avgStat) / avgStat
  }

  def produceComparisonToAverage(
    numPlayers: Int,
    offset: Double
  )(
    playerRange: AnalyzedRushRange,
    averageRange: AnalyzedRushRange
  ): NormalizedRushRange = {
    require(playerRange.range == averageRange.range)

    val avgAverageRush = averageRange.averageRush
    val avgTouchdownRate = averageRange.numTouchdowns.toDouble / averageRange.numRushes

    val avgNumRushes = averageRange.numRushes / numPlayers
    val avgNumTouchdowns = averageRange.numTouchdowns / numPlayers

    val playerAverageRush = playerRange.averageRush
    val playerTouchdownRate = playerRange.numTouchdowns.toDouble / playerRange.numRushes

    val playerNumRushes = playerRange.numRushes
    val playerNumTouchdowns = playerRange.numTouchdowns

    NormalizedRushRange(
      playerRange.range,
      normalizeMetricToAverage(playerAverageRush, avgAverageRush, offset),
      normalizeMetricToAverage(playerNumRushes, avgNumRushes, offset),
      normalizeMetricToAverage(playerTouchdownRate, avgTouchdownRate, offset),
      normalizeMetricToAverage(playerNumTouchdowns, avgNumTouchdowns, offset)
    )
  }

}