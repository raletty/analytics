package ra.analysis.rushing.data

import cats.implicits._
import scala.collection.breakOut

import scala.util.Try

case class RushingDatum(
    playerName: String,
    team: String,
    quarter: Int,
    timeLeft: String,
    down: Int,
    yardsToGo: Int,
    location: String,
    teamScore: Int,
    oppScore: Int,
    yardsRushed: Double
) {

  import RushingDatum._

  val locationToYardsLeft: Int = location.split(" ").toList match {
    case side :: yardLine :: Nil if fieldToTeamMap.getOrElse(side, "") == team =>
      100 - yardLine.toOptInt.getOrElse(Integer.MIN_VALUE)
    case side :: yardLine :: Nil =>
      yardLine.toOptInt.getOrElse(Integer.MIN_VALUE)
    case yardLine :: Nil =>
      yardLine.toOptInt.getOrElse(Integer.MIN_VALUE)
  }

  def yardsLeftBucket(numBuckets: Int): Option[YardRange] = {
    YardRange.
      breakIntoYardageIntervals(YardRange(1, 100), numBuckets).
      find { _.contains(locationToYardsLeft) }
  }

  def scoredTouchdown: Boolean = (locationToYardsLeft - yardsRushed) == 0.0

  def totalTimeLeft: String = {
    val timeSplit = timeLeft.split(":")
    s"${timeSplit(0) + quarter * 15}:${timeSplit(1)}"
  }

}

object RushingDatum {

  implicit class StatParsingOps(stat: String) {
    def toOptStr: Option[String] = Some(stat)
    def toOptInt: Option[Int] = Try(stat.toInt).toOption
    def toOptDouble: Option[Double] = Try(stat.toDouble).toOption
  }

  private final val fieldTickers = Seq(
    "CRD", "ATL", "RAV", "BUF",
    "CAR", "CHI", "CIN", "CLE",
    "DAL", "DEN", "DET", "GNB",
    "HTX", "CLT", "JAX", "KAN",
    "RAM", "MIA", "MIN", "NWE",
    "NOR", "NYG", "NYJ", "RAI",
    "PHI", "PIT", "SDG", "SFO",
    "SEA", "TAM", "OTI", "WAS"
  )

  private final val teamTickers = Seq(
    "ARI", "ATL", "BAL", "BUF",
    "CAR", "CHI", "CIN", "CLE",
    "DAL", "DEN", "DET", "GNB",
    "HOU", "IND", "JAX", "KAN",
    "STL", "MIA", "MIN", "NWE",
    "NOR", "NYG", "NYJ", "OAK",
    "PHI", "PIT", "SDG", "SFO",
    "SEA", "TAM", "TEN", "WAS"
  )

  val fieldToTeamMap: Map[String, String] = (fieldTickers zip teamTickers)(breakOut)
  val tupler10 = (optionInstance.tuple10[String, String, Int, String, Int, Int, String, Int, Int, Double, AnyVal] _).tupled
  private val scores = "(\\d+)-(\\d+)".r

  def splitLine(line: String) = {
    val lineSplit = line.split(",", -1)
    val scores(teamScore, oppScore) = lineSplit(7)
    (
      lineSplit(0).toOptStr,
      lineSplit(1).toOptStr,
      lineSplit(2).toOptInt,
      lineSplit(3).toOptStr,
      lineSplit(4).toOptInt,
      lineSplit(5).toOptInt,
      lineSplit(6).toOptStr,
      teamScore.toOptInt,
      oppScore.toOptInt,
      lineSplit(8).toOptDouble
    )
  }

  def parseData(records: Seq[String]): Seq[RushingDatum] = {
    records.par.
      map { splitLine }.
      map { tupler10 }.
      collect { case Some(a) => a }.
      map { (RushingDatum.apply _).tupled }.seq
  }

  private def produceRushMetricsByRange(rushingData: Seq[RushingDatum]): (Double, Int, Int) = {
    (
      rushingData.map(_.yardsRushed).sum / rushingData.size,
      rushingData.size,
      rushingData.count(_.scoredTouchdown)
    )
  }

  def findAverageRushByLocation(numBuckets: Int)(rushingData: Seq[RushingDatum]): Seq[AnalyzedRushRange] = {
    // FIXME: Yards to go is yds to go in down, not yards to go for td
    rushingData.
      groupBy { _.yardsLeftBucket(numBuckets) }.
      collect { case (Some(yardsLeft), groupedData) => (yardsLeft, groupedData) }.
      mapValues { produceRushMetricsByRange }.toSeq.
      sortBy { case (yardsLeft, _) => yardsLeft }.
      map { case (yardsLeft, (avgRush, rushes, tds)) => AnalyzedRushRange(yardsLeft, avgRush, rushes, tds) }
  }
}