package ra.analysis.ranking.pagerank.gradient

import ra.analysis.ranking.pagerank.models.Sport

trait GradientBuilder[A <: Sport] {

  val numWeeks: Int
  val isHome: Double
  val gameDifference: Double
  val diffDifference: Double
  val tierInterval: Int
  val tierCutoff: Int

  def evaluateGameWeight( week: Int, away: Boolean, differential: Int ): Double

  /** A basic tiering on point differential -- lower differential = more penalty.
   *
   *  @param differential --
   *  @return             -- penalty to remove on edge weight
   */
  def subDifferential( differential: Int ): Double = {
    val diffTier = roundToN( tierInterval )( differential ) / tierInterval
    if ( diffTier >= tierCutoff ) 0.0 else ( tierCutoff - diffTier ) * diffDifference
  }

  def roundToN( base: Int )( num: Int ): Int = ( num + ( base - 1 ) ) / base * base

}
