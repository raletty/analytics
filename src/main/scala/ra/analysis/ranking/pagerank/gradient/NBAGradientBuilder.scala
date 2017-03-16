package ra.analysis.ranking.pagerank.gradient

import ra.analysis.ranking.pagerank.models.NBA

class NbaGradientBuilder extends GradientBuilder[NBA] {
  val numWeeks: Int = 82

  val tierInterval: Int = 5

  val tierCutoff: Int = 25

  val isHome: Double = 506.0 / 724.0

  val gameDifference: Double = 0.005

  val diffDifference: Double = 0.04

  def evaluateGameWeight(week: Int, away: Boolean, differential: Int): Double = {
    val weekWeight = 1 - (numWeeks - week) * gameDifference
    val awayWeight = if (away) 1.0 else isHome
    val differentialWeight = 1 - subDifferential(differential)
    weekWeight * awayWeight * differentialWeight
  }
}
