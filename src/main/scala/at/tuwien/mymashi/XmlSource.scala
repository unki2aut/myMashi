package at.tuwien.mymashi

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 12.04.11
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */


import java.io._
import java.net._
import io.Source
import xml.parsing.ConstructingParser
import xml.{Text, NodeSeq}

class XmlSource(source: String) {

  private val url = new URL(source)

  def exists = try {
    url.openConnection match {
      case con: HttpURLConnection => {
        con.setRequestMethod("HEAD")
        (con.getResponseCode == HttpURLConnection.HTTP_OK)
      }
      case _ => false
    }
  } catch {
    case _ => false
  }

  def content: NodeSeq = try {
    ConstructingParser.fromSource(Source.fromURL(url), false).document
  } catch {
    case e => Text(e.toString)
  }
}