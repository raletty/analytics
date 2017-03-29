package ra.analysis.ranking.pagerank.rdd

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.{VertexRDD, Graph, TripletFields}
import ra.analysis.ranking.pagerank.models._

import scala.reflect.ClassTag

object EdgeWeightedPageRank {

  /**
    * Runs a weighted version of PageRank applying the following changes:
    * -- runs a gradient across the season weighting recent games higher
    * -- takes into account away wins as being more impressive
    * -- uses strength of victory in a game as another modification on rank
    *
    * GameEdgeAttribute => (week, away, score difference)
    *
    * @param inputGraph     -- input graph where each edge has a GameEdgeAttribute
    * @param gameGradient   -- a mapping of week numbers to score weights
    * @param startingWeight -- reset probability on a given vertex
    * @tparam VD -- unused vertex attribute
    * @return -- PageRank graph
    */
  def run[VD: ClassTag](
    gameGradient: Broadcast[TeamGradient],
    startingWeight: Double = 0.15
  )(
    inputGraph: Graph[VD, GameEdgeAttribute],
    numIterations: Int
  ): Graph[Double, GameEdgeAttribute] = {

    var weightedRankGraph: Graph[Double, GameEdgeAttribute] = inputGraph.
      mapVertices { (id, attr) => startingWeight }

    var prevWeightedRankGraph = weightedRankGraph
    var iteration = 0

    while (iteration < numIterations) {
      weightedRankGraph.cache()

      val rankUpdates: VertexRDD[Double] = weightedRankGraph.aggregateMessages[Double](
        ctx => ctx.sendToDst {
          val gradientMap = gameGradient.value
          val gradientMapForVertex = gradientMap.getOrElse(ctx.srcId, Map[Int, Double]())
          val outDegree = gradientMapForVertex.keySet.size
          ctx.srcAttr * (gradientMapForVertex.getOrElse(ctx.attr.week, startingWeight) / outDegree)
        },
        _ + _,
        TripletFields.Src
      )

      prevWeightedRankGraph = weightedRankGraph
      weightedRankGraph = weightedRankGraph.joinVertices(rankUpdates) {
        (id, _, msgSum) => startingWeight + (1.0 - startingWeight) * msgSum
      }.cache()

      weightedRankGraph.edges.foreachPartition(x => {}) // also materializes weightedRankGraph.vertices
      prevWeightedRankGraph.vertices.unpersist(blocking = false)
      prevWeightedRankGraph.edges.unpersist(blocking = false)
      iteration += 1
    }

    weightedRankGraph
  }

}
