package ra.analysis.ranking.pagerank.frame

import org.apache.spark.sql.SparkSession
import org.graphframes.GraphFrame
import ra.analysis.ranking.pagerank.frame.PageRankFrameUtils._
import ra.analysis.ranking.pagerank.models.{ GameOps, Sport, ScoreMap }

object PageRankScoresWithFrames {

  def runPageRankWithFrames[A <: Sport: GameOps](
    resetProb: Double,
    iterations: Int
  ): ScoreMap = {

    val gameOps = implicitly[GameOps[A]]
    val gameLines = gameOps.gameLines
    val teams = gameOps.teams

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("PageRankScoresWithFrames")
      .getOrCreate()

    import spark.implicits._

    val teamNamesFrame = teams.map(TeamName.apply).toDF()
    val gameOutcomesFrame = gameLines.
      map(gameOps.generateGameDescription).
      collect { case Some(desc) => desc }.
      map { desc => (desc.loser, desc.winner) }.
      map(GameOutcome.tupled).toDF("src", "dst")
    val inputGraphFrame = GraphFrame(teamNamesFrame, gameOutcomesFrame)

    val iterationScoresOutput = generateIterationScoresMapFromFrame(
      spark,
      inputFrame = inputGraphFrame,
      teamNamesFrame = teamNamesFrame,
      resetProb = resetProb,
      iters = iterations
    )

    iterationScoresOutput
  }

}
