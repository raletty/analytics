package ra.analysis.ranking.pagerank.frame

import ra.analysis.util.LoadUtils

object WeightedPageRankScoresWithFrames extends LoadUtils {

  def main(args: Array[String]) {

    val gameLines = getData("/ra/analysis/ranking/full_2015_game_scores")

  }

}
