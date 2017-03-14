package ra.analysis.ranking.pagerank.frame

import org.apache.spark.sql.SparkSession
import org.graphframes.GraphFrame
import ra.analysis.ranking.pagerank.frame.PageRankFrameUtils._
import ra.analysis.util.LoadUtils

object PageRankScoresWithFrames extends LoadUtils {

  def main(args: Array[String]) {

    val gameLines = getData("/ra/analysis/ranking/full_2015_game_scores")
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("PageRankScoresWithFrames")
      .getOrCreate()

    import spark.implicits._

    val teams = gameLines.flatMap { line => List(line.split(",")(4), line.split(",")(6)) }.distinct

    val teamNamesFrame = teams.map(TeamName.apply).toDF()
    val gameOutcomesFrame = gameLines.map(generateGameEdge).map(GameOutcome.tupled).toDF("src", "dst")
    val inputGraphFrame = GraphFrame(teamNamesFrame, gameOutcomesFrame)

    val iterationScoresOutput = generateIterationScoresMapFromFrame(
      spark,
      inputFrame = inputGraphFrame,
      teamNamesFrame = teamNamesFrame,
      resetProb = 0.4,
      iters = 12
    )

    iterationScoresOutput foreach println
  }

}
