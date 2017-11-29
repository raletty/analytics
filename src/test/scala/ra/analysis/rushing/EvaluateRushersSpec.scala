package ra.analysis.rushing

import org.scalactic.TolerantNumerics
import org.scalatest.{ Inside, Matchers, FlatSpec }
import ra.analysis.rushing.data.{ YardRange, AnalyzedRushRange, RushingDatum }
import ra.analysis.util.LoadUtils.getData

class EvaluateRushersSpec extends FlatSpec with Inside with Matchers {

  implicit val doubleEquality = TolerantNumerics.tolerantDoubleEquality( 0.00001 )

  it should "produce metrics by range correctly for Spencer Ware" in {

    val groupedWare: Seq[AnalyzedRushRange] = EvaluateRushersFixture
      .wareForceTrauma
      .groupBy( _.playerName )
      .mapValues { RushingDatum.findAverageRushByLocation( 10 ) }
      .toSeq.flatMap( _._2 )

    val Seq( s1, s2, s3, s4, s5, s6, s7, s8, s9, s10 ) = groupedWare

    inside( s1 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 1; end shouldEqual 10 }
        avgRush shouldEqual 1.904761
        numRushes shouldEqual 21
        numTds shouldEqual 3
    }

    inside( s2 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 11; end shouldEqual 20 }
        avgRush shouldEqual 2.428571
        numRushes shouldEqual 7
        numTds shouldEqual 0
    }

    inside( s3 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 21; end shouldEqual 30 }
        avgRush shouldEqual 5.277777
        numRushes shouldEqual 18
        numTds shouldEqual 0
    }

    inside( s4 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 31; end shouldEqual 40 }
        avgRush shouldEqual 3.578947
        numRushes shouldEqual 19
        numTds shouldEqual 0
    }

    inside( s5 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 41; end shouldEqual 50 }
        avgRush shouldEqual 6.857142
        numRushes shouldEqual 21
        numTds shouldEqual 0
    }

    inside( s6 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 51; end shouldEqual 60 }
        avgRush shouldEqual 5.36000
        numRushes shouldEqual 25
        numTds shouldEqual 0
    }

    inside( s7 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 61; end shouldEqual 70 }
        avgRush shouldEqual 5.023809
        numRushes shouldEqual 42
        numTds shouldEqual 0
    }

    inside( s8 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 71; end shouldEqual 80 }
        avgRush shouldEqual 3.155555
        numRushes shouldEqual 45
        numTds shouldEqual 0
    }

    inside( s9 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 81; end shouldEqual 90 }
        avgRush shouldEqual 4.500000
        numRushes shouldEqual 14
        numTds shouldEqual 0
    }

    inside( s10 ) {
      case AnalyzedRushRange( yardRange, avgRush, numRushes, numTds ) =>
        inside( yardRange ) { case YardRange( start, end ) => start shouldEqual 91; end shouldEqual 100 }
        avgRush shouldEqual 2.500000
        numRushes shouldEqual 4
        numTds shouldEqual 0
    }
  }

}

object EvaluateRushersFixture extends EvaluateRushersFixture

trait EvaluateRushersFixture {

  val data = getData( "/ra/analysis/rushing/top_25_rushers_2016" )
  val parsedData = RushingDatum.parseData( data.drop( 1 ) )

  val blountForceTrauma = parsedData.filter( _.playerName == "LeGarrette Blount" )
  val wareForceTrauma = parsedData.filter( _.playerName == "Spencer Ware" )

}