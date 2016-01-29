package ra.analysis.ranking.pagerank

import org.apache.spark.graphx.{TripletFields, Graph}
import ra.analysis.ranking._
import scala.reflect.ClassTag

object EdgeWeightedPageRank {

//  def runWeightedPageRank[VD: ClassTag](
//    inputGraph: Graph[VD, GameEdgeAttribute],
//    gameGradient: GameGradient
//  ) : Graph[Double, Double] = {
//
//    var rankGraph: Graph[Double, Double] = graph
//      .outerJoinVertices(graph.outDegrees) { (vid, vdata, deg) => deg.getOrElse(0) }
//      .mapTriplets( e => 1.0 / e.srcAttr, TripletFields.Src )
//      .mapVertices { (id, attr) => if (!(id != src && personalized)) resetProb else 0.0 }
//  }

}
