package code.mymashi;

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 12.04.11
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */

import java.net._
import xml._
import net.liftweb.common.Logger
import java.io.IOException

class XmlSource(source: String) extends Logger {
  private val url = new URL(source)
  private var data: Elem = _
  var lastModified = 0l

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

   def fetch: String = {
    val con = url.openConnection
    lastModified = con.asInstanceOf[HttpURLConnection].getLastModified
    val in = con.getInputStream
    val tmp = scala.io.Source.fromInputStream(in).mkString
    in.close

    tmp
  }

  def content: Option[Node] = try {
    data = XML.loadString(this.fetch)

    if(data == null) {
      error("XML.load returns NULL")
      None
    } else {
      Some(data)
    }
  } catch {
    case ioX: IOException => error("IOException: "+ioX.getMessage); ioX.printStackTrace; None
    case spX: SAXParseException => error("SAXParseException: "+spX.getMessage); None
    case e: Exception => error(e.getClass.getCanonicalName+": "+e.getMessage); None
  }
}

