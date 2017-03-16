package ra.analysis.ranking.pagerank.gradient

import ra.analysis.ranking.pagerank.models.NFL

class NFLGradientBuilder extends GradientBuilder[NFL] {

  // Home teams had a record of 136-117 (away wins to weighted ~1.1623 higher).
  val isHome = 117.0 / 136.0

  // Number of weeks in an NFL season.
  val numWeeks = 17

  // Discount each previous week by 2%.
  val gameDifference = 0.02

  // Discount closer games by 4% per tier.
  val diffDifference = 0.04

  // Touchdown + PAT seems valid for a tier differential.
  val tierInterval = 7

  // Number of tiers.
  val tierCutoff = 5

  def evaluateGameWeight(week: Int, away: Boolean, differential: Int): Double = {
    val weekWeight = 1 - (numWeeks - week) * gameDifference
    val awayWeight = if (away) 1.0 else isHome
    val differentialWeight = 1 - subDifferential(differential)
    weekWeight * awayWeight * differentialWeight
  }

}
