package ra.analysis.util

import java.io.{ File, FileOutputStream, InputStream }

import scala.io.Source
import resource._

object IOUtils {

  def getData( filename: String ): Seq[String] = {
    val stream: InputStream = getClass.getResourceAsStream( filename )
    Source.fromInputStream( stream ).getLines().toSeq
  }

  def writeData( output: String, filename: String ): Unit = {
    val byteArray = output.getBytes
    val file = new File( filename )
    managed( new FileOutputStream( file ) ).acquireAndGet( _.write( byteArray ) )
  }

  def writeData( output: Seq[String], filename: String ): Unit = {
    val byteArray = output.mkString( "\n" ).getBytes
    val file = new File( filename )
    managed( new FileOutputStream( file ) ).acquireAndGet( _.write( byteArray ) )
  }

}
