package ra.analysis.ranking.pagerank

import org.apache.spark.graphx.{TripletFields, Graph}
import org.apache.spark.Logging
import ra.analysis.ranking._
import scala.reflect.ClassTag

object EdgeWeightedPageRank extends Logging {

  /**
   * Runs a weighted version of PageRank applying the following changes:
   *  -- runs a gradient across the season weighting recent games higher
   *  -- takes into account away wins as being more impressive
   *  -- uses strength of victory in a game as another modification on rank
   *
   * GameEdgeAttribute => (week, away, score difference)
   *
   * @param inputGraph -- input graph where each edge has a GameEdgeAttribute
   * @param gameGradient -- a mapping of week numbers to score weights
   * @param startingWeight -- reset probability on a given vertex
   * @tparam VD -- unused vertex attribute
   * @return -- PageRank graph
   */
  def runWeightedPageRank[VD: ClassTag](
    inputGraph: Graph[VD, GameEdgeAttribute],
    gameGradient: TeamGradient,
    startingWeight: Double = 0.15,
    numIterations: Int
  ) : Graph[Double, GameEdgeAttribute] = {

    var weightedRankGraph: Graph[Double, GameEdgeAttribute] = inputGraph.mapVertices {
      (id, attr) => startingWeight
    }

    var prevWeightedRankGraph = weightedRankGraph
    var iteration = 0

    while (iteration < numIterations) {
      weightedRankGraph.cache()

      val rankUpdates = weightedRankGraph.aggregateMessages[Double](
        ctx => ctx.sendToDst(
          gameGradient.getOrElse(ctx.srcId, Map[Int,Double]())
                      .getOrElse(ctx.attr.week, startingWeight)
        ),
        _ + _, TripletFields.Src
      )

      prevWeightedRankGraph = weightedRankGraph
      weightedRankGraph = weightedRankGraph.joinVertices(rankUpdates) {
        (id, _, msgSum) => startingWeight + (1 - startingWeight) * msgSum
      }.cache()

      weightedRankGraph.edges.foreachPartition(x => {}) // also materializes rankGraph.vertices
      log.info(s"PageRank finished iteration $iteration.")
      prevWeightedRankGraph.vertices.unpersist(blocking = false)
      prevWeightedRankGraph.edges.unpersist(blocking = false)
      iteration += 1
    }

    weightedRankGraph
  }

}
