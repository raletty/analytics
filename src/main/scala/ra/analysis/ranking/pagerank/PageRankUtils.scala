package ra.analysis.ranking.pagerank

import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._
import org.apache.spark.rdd.RDD
import ra.analysis.ranking._
import ra.analysis.ranking.pagerank.gradient.GradientBuilder

object PageRankUtils {

  /**
   * Given an input game line, create an edge directing the loser to the winner.
   * @param gameLine -- line schema: [week, day, date, type, winner, away, loser, winner pts, loser pts]
   * @return case class encompassing [loser, winner, week, away, differential]
   */
  def generateGameDescription(gameLine: String): GameDescription = {
    val split = gameLine.split(",")
    GameDescription(vertexIdFromName(split(6)),
                    vertexIdFromName(split(4)),
                    split(0).toInt,
                    split(5).nonEmpty,
                    split(7).toInt - split(8).toInt)
  }

  /**
   * Given an input game line, create an edge directing the loser to the winner.
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
   * @param descs -- sequence of game descriptions to transform into a game gradient
   * @param builder -- object handling the mapping of weeks to weights
   * @return game gradient for a given team
   */
  def buildTeamGradientEntry(descs: Seq[GameDescription], builder: GradientBuilder): GameGradient = {
    descs.map { case GameDescription(_, _, week, away, difference) =>
      (week, builder.evaluateGameWeight(week, away, difference))
    }.toMap
  }

  /**
   * Given an input game line, generate a non-attribute edge pointing the loser to the winner.
   * @param gameLine -- input line to draw edge from
   * @return -- vertex tuple simulating an edge (graph will be created from a set of these)
   */
  def generateGameEdge(gameLine: String): (VertexId, VertexId) = {
    val split = gameLine.split(",")
    (vertexIdFromName(split(6)), vertexIdFromName(split(4)))
  }

  /**
   * Standardize team name and create a vertex ID from it.
   * @param name -- input team name
   * @return -- vertex ID
   */
  def vertexIdFromName(name: String): VertexId = {
    name.replaceAll("\\s", "").toLowerCase.hashCode
  }

  /**
   * Calculate the page rank scores for each vertex on each iteration.
   * @param inputGraph -- input win-loss graph
   * @param teamNamesRDD -- mapping of vertex ID to team name
   * @param resetProb -- starting score on each vertex (non-personalized)
   * @param iters -- number of iterations run
   * @return a map pointing a team to a list of iteration scores
   */
  def generateIterationScoresMap(
    inputGraph: Graph[Double, Int],
    teamNamesRDD: VertexRDD[String],
    resetProb: Double,
    iters: Int
  ): ScoreMap = {
    var rankOutputGraph: Graph[Double, Double] = null
    var iterationScoresMap: ScoreMap = Map()
    teamNamesRDD.cache()

    for (i <- 0 until iters) {
      rankOutputGraph = PageRank.run(inputGraph, i, resetProb)
      val iterationScoreMap = rankOutputGraph.outerJoinVertices(teamNamesRDD) {
        case (vid, score, Some(name)) => (name, score)
      }.vertices.map(_._2).collectAsMap()

      iterationScoresMap = iterationScoresMap ++ iterationScoreMap.map {
        case (k, v) => k -> (iterationScoresMap.getOrElse(k, List()) :+ v)
      }
    }

    iterationScoresMap
  }

  /**
   * An alternative to the above where iterations are run until the scores converge with some tolerance.
   * @param inputGraph -- input win-loss graph
   * @param teamNamesRDD -- mapping of vertex ID to team name
   * @param tolerence -- epsilon within which final interation difference should lie
   * @param resetProb -- starting score on each vertex (non-personalized)
   * @return a map pointing a team to a list of iteration scores
   */
  def generateConvergenceRun(
    inputGraph: Graph[Double, Int],
    teamNamesRDD: VertexRDD[String],
    tolerence: Double = 0.001,
    resetProb: Double = 0.4
  ): RDD[(String, Double)] = {
    val rankOutputGraph = PageRank.runUntilConvergence(inputGraph, tolerence, resetProb)
    val namedOutputGraph = rankOutputGraph.outerJoinVertices(teamNamesRDD) {
      case (_, score, Some(name)) => (name, score)
    }

    namedOutputGraph.vertices.map(_._2).coalesce(1).sortBy(_._2, ascending = false)
  }

}
