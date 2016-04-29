package ra.analysis.ranking.pagerank.gradient

trait GradientBuilder {

  val numWeeks: Int
  val isHome: Double
  val weekDifference: Double
  val diffDifference: Double

  def subDifferential(differential: Int): Double

  def evaluateGameWeight(week: Int, away: Boolean, differential: Int): Double

  def roundToN(base: Int)(num: Int): Int = (num + (base - 1)) / base * base

}
