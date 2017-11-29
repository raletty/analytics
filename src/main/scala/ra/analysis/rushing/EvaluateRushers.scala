package ra.analysis.rushing

import ra.analysis.rushing.data.{ RushRange, NormalizedRushRange, AnalyzedRushRange, RushingDatum }
import ra.analysis.util.LoadUtils.getData
import scala.collection.breakOut

object EvaluateRushers {

  def main( args: Array[String] ) = {

    val rushingData: Seq[String] = getData( "/ra/analysis/rushing/significant_rushing_plays_total_1994_2016" )
    // val rushingData: Seq[String] = getData( "/ra/analysis/rushing/21st_century_rushers" )
    // val rushingData: Seq[String] = getData( "/ra/analysis/rushing/top_25_rushers_2016" )

    /** Player,Team,Quarter,Time Left,Down,Yards To Go,Location,Score,Yards Rushed
     *  Shaun Alexander,SEA,3,2:06,4,1,SEA 44,10-21,50
     *  Edgerrin James,ARI,2,13:07,1,10,CRD 12,0-0,18
     */

    val parsedData: Seq[RushingDatum] = RushingDatum.parseData( rushingData.drop( 1 ) )
    val numRushers: Int = parsedData.map( _.playerName ).distinct.size
    val yardageBuckets: Int = 50

    val averageRushRanges: Seq[AnalyzedRushRange] =
      RushingDatum.findAverageRushByLocation( yardageBuckets )( parsedData )

    val playerRushByLocation: Map[String, Seq[AnalyzedRushRange]] =
      parsedData.
        filter( data => PlayerGroups.historical.contains( data.playerName ) ).
        groupBy( _.playerName ).
        mapValues( RushingDatum.findAverageRushByLocation( yardageBuckets ) )

    val normalizedPlayerRanges: Seq[( String, Seq[NormalizedRushRange] )] =
      playerRushByLocation.
        map {
          case ( playerName, playerRushRanges ) =>
            val toNormalizedRushRange = ( AnalyzedRushRange.produceComparisonToAverage( numRushers, 1.0 ) _ ).tupled
            val normalizedRanges = ( playerRushRanges zip averageRushRanges ).map { toNormalizedRushRange }
            ( playerName, normalizedRanges )
        } ( breakOut )

    println( "Normalized Ranges: " )
    formatScores( normalizedPlayerRanges ).foreach( println )
  }

  def formatScores( normalizedPlayerRanges: Seq[( String, Seq[RushRange] )] ): Seq[String] = {
    for {
      normalizedPlayerRange <- normalizedPlayerRanges
      ( name, ranges ) = normalizedPlayerRange
      range <- ranges
    } yield s"$name,${range.csvString}"
  }

}

object PlayerGroups {

  val historical = Seq(
    "Shaun Alexander", "Tiki Barber", "Jerome Bettis", "Jamaal Charles",
    "Corey Dillon", "Warrick Dunn", "Maurice Jones-Drew", "Marshall Faulk",
    "Matt Forte", "Arian Foster", "Eddie George", "Frank Gore",
    "Ahman Green", "Priest Holmes", "Steven Jackson", "Edgerrin James",
    "Chris Johnson", "Jamal Lewis", "Marshawn Lynch", "Curtis Martin",
    "DeMarco Murray", "LeSean McCoy", "Adrian Peterson", "Clinton Portis",
    "Fred Taylor", "LaDainian Tomlinson", "Brian Westbrook", "Ricky Williams"
  )

  val recent = Seq(
    "Cedric Benson", "Jamaal Charles", "Maurice Jones-Drew", "Matt Forte", "Arian Foster",
    "Fred Jackson", "Chris Johnson", "Marshawn Lynch", "Doug Martin", "DeMarco Murray",
    "LeSean McCoy", "Adrian Peterson", "Ray Rice", "Jonathan Stewart", "Michael Turner",
    "DeAngelo Williams"
  )

}
