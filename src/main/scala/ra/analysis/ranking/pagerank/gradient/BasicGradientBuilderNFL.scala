package ra.analysis.ranking.pagerank.gradient

class BasicGradientBuilderNFL extends GradientBuilder {

  val isHome = 117.0 / 136.0
  val numWeeks = 17
  val weekDifference = 0.02
  val diffDifference = 0.04

  def subDifferential(differential: Int): Double = {
    val diffTier = roundToN(7)(differential) / 7
    if (diffTier >= 5) 0.0 else (5 - diffTier) * diffDifference
  }

  def evaluateGameWeight(week: Int, away: Boolean, differential: Int): Double = {
    val weekWeight = 1 - (numWeeks - week) * weekDifference
    val awayWeight = if (away) 1.0 else isHome
    val differentialWeight = 1 - subDifferential(differential)
    weekWeight * awayWeight * differentialWeight
  }

}
