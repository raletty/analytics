package ra.analysis.ranking.pagerank

import org.apache.spark.graphx._
import ra.analysis.ranking.pagerank.models._
import ra.analysis.ranking.pagerank.gradient.GradientBuilder

trait PageRankUtils {

  /**
   * Given an input game line, create an edge directing the loser to the winner.
   *
   * @param gameLine -- line schema: [week, day, date, type, winner, away, loser, winner pts, loser pts]
   * @return case class encompassing [loser, winner, week, away, differential]
   */
  def generateGameDescription(gameLine: String): GameDescription = {
    val split = gameLine.split(",")
    GameDescription(
      loser     = vertexIdFromName(split(6)),
      winner    = vertexIdFromName(split(4)),
      week      = split(0).toInt,
      away      = split(5).nonEmpty,
      scoreDiff = split(7).toInt - split(8).toInt
    )
  }

  /**
   * Given an input game line, create an edge directing the loser to the winner.
   *
   * @param gameDesc -- relevant fields from a game line
   * @return win-loss edge
   */
  def generateWeightedGameEdge(gameDesc: GameDescription): Edge[GameEdgeAttribute] = {
    val edgeAttr = GameEdgeAttribute(gameDesc.week, 1.0)
    Edge(gameDesc.loser, gameDesc.winner, edgeAttr)
  }

  /**
   * Given a game description, create a GameGradient that describes how each week should be
   * weighted given certain attributes.
   *
   * @param descs -- sequence of game descriptions to transform into a game gradient
   * @param builder -- object handling the mapping of weeks to weights
   * @return game gradient for a given team
   */
  def buildTeamGradientEntry(builder: GradientBuilder)(descs: Seq[GameDescription]): GameGradient = {
    descs.map { case GameDescription(_, _, week, away, difference) =>
      (week, builder.evaluateGameWeight(week, away, difference))
    }.toMap
  }

  /**
   * Given an input game line, generate a non-attribute edge pointing the loser to the winner.
   *
   * @param gameLine -- input line to draw edge from
   * @return -- vertex tuple simulating an edge (graph will be created from a set of these)
   */
  def generateGameEdge(gameLine: String): (VertexId, VertexId) = {
    val split = gameLine.split(",")
    (vertexIdFromName(split(6)), vertexIdFromName(split(4)))
  }

  /**
   * Standardize team name and create a vertex ID from it.
 *
   * @param name -- input team name
   * @return -- vertex ID
   */
  def vertexIdFromName(name: String): VertexId = name.replaceAll("\\s", "").toLowerCase.hashCode

}
