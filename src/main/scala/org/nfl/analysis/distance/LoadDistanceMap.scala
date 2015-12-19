package org.nfl.analysis.distance

import java.io.InputStream

object LoadDistanceMap {
  lazy val resourceFileName = "org/nfl/analysis/distance/nfl_stadium_distances.txt"
  lazy val nflDistanceMap = Array.ofDim[String](32, 32)

  def loadMapToArray() = {
    val resourceStream : InputStream = getClass.getResourceAsStream(resourceFileName)


  }

  def createDistanceMap() = {

  }

}
