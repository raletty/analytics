package ra.analysis

package object ranking {

  // Edge attribute in rank graph.
  case class GameEdgeAttribute(week: Int, away: Boolean, scoreDiff: Double)

  // Gradient defining weight increase throughout season.
  case class GameGradient(numWeeks: Int, weightIncrease: Double)

  // Shorthand type for iteration scores output.
  type ScoreMap = Map[String, List[Double]]

}
