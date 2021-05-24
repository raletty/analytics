package ra.analysis.trades

import ra.analysis.util.IOUtils._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object CreateTradeGraph {

  val teamNameMap: Map[String, String] = Map(
    "Atlanta Hawks" -> "ATL", "Boston Celtics" -> "BOS", "Brooklyn Nets" -> "BRK",
    "New Jersey Nets" -> "BRK", "Charlotte Hornets" -> "CHA", "Charlotte Bobcats" -> "CHA",
    "Chicago Bulls" -> "CHI", "Cleveland Cavaliers" -> "CLE", "Dallas Mavericks" -> "DAL",
    "Denver Nuggets" -> "DEN", "Detroit Pistons" -> "DET", "Golden State Warriors" -> "GSW",
    "Houston Rockets" -> "HOU", "Indiana Pacers" -> "IND", "Los Angeles Clippers" -> "LAC",
    "Los Angeles Lakers" -> "LAL", "Memphis Grizzlies" -> "MEM", "Vancouver Grizzlies" -> "MEM",
    "Miami Heat" -> "MIA", "Milwaukee Bucks" -> "MIL", "Minnesota Timberwolves" -> "MIN",
    "New Orleans Pelicans" -> "NO", "New Orleans Hornets" -> "NO", "New Orleans/Oklahoma City Hornets" -> "NO",
    "New York Knicks" -> "NYK", "Oklahoma City Thunder" -> "OKC", "Seattle SuperSonics" -> "OKC",
    "Orlando Magic" -> "ORL", "Philadelphia 76ers" -> "PHI", "Phoenix Suns" -> "PHO",
    "Portland Trail Blazers" -> "POR", "Sacramento Kings" -> "SAC", "San Antonio Spurs" -> "SA",
    "Toronto Raptors" -> "TOR", "Utah Jazz" -> "UTA", "Washington Wizards" -> "WAS" )

  def main( args: Array[String] ): Unit = {

    val inputLines = getData( "/ra/analysis/trades/trades_2000_present.csv" )

    val trades = inputLines
      .map( _.split( "," )( 1 ) )
      .map( _.split( "\\|" ).map( teamNameMap ).sorted )
      .flatMap( _.combinations( 2 ).toList.map( _.mkString( "-" ) ) )

    val tradeCounts = trades
      .groupBy( identity )
      .mapValues( _.map( _ => 1 ).sum )
      .toSeq.sortBy( _._2 )

    val outputFile = "trade_counts_2000_present.csv"
    writeData( createTradeJson( tradeCounts ), outputFile )
  }

  def createTradeJson( tradeCounts: Seq[( String, Int )] ): String = {
    val teamIndexMap = teamNameMap.values.zipWithIndex.toMap

    val teams = teamIndexMap.map {
      case ( name, group ) =>
        ( "name" -> name ) ~ ( "group" -> group )
    }

    val links = tradeCounts.map {
      case ( tradePair, count ) =>
        val Array( team1, team2 ) = tradePair.split( "-" )
        ( "source" -> teamIndexMap( team1 ) ) ~
          ( "target" -> teamIndexMap( team2 ) ) ~
          ( "weight" -> count )
    }

    val json = ( "nodes" -> teams ) ~ ( "links" -> links )

    compact( render( json ) )
  }

}
