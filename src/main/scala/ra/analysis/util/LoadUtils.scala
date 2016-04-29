package ra.analysis.util

import java.io.InputStream
import scala.io.Source

trait LoadUtils {

  def getData(filename: String) : Seq[String] = {
    val stream: InputStream = getClass.getResourceAsStream(filename)
    Source.fromInputStream(stream).getLines().toSeq
  }

}
