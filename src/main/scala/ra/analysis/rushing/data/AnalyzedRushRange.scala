package ra.analysis.rushing.data

case class AnalyzedRushRange(
  range: YardRange,
  averageRush: Double,
  numRushes: Int,
  numTouchdowns: Int
) {
  override def toString: String =
    s"[range: ${range.rangeString}, averageRush: $averageRush, rushes: $numRushes, touchdowns: $numTouchdowns]"
}
