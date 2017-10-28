package ra.analysis.rushing.data

case class YardRange( start: Int, end: Int ) {
  def contains( yard: Int ): Boolean = yard >= start && yard <= end
  def rangeString: String = s"$start-$end"
}

object YardRange {

  implicit def ordering: Ordering[YardRange] = Ordering.by( _.start )

  def breakIntoYardageIntervals( yardRange: YardRange, numIntervals: Int ): Seq[YardRange] = {
    require( yardRange.end % numIntervals == 0 )
    val step = yardRange.end / numIntervals
    ( 0 until numIntervals ).
      map { _ * step + 1 }.
      map { start => YardRange( start, start + step - 1 ) }
  }

}
