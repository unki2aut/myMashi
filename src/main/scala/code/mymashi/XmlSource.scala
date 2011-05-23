package code.mymashi {

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 12.04.11
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */


import java.net._
import xml._
import java.util.Date
import net.liftweb.common.{Full, Empty, Box, Logger}

class XmlSource(source: String) extends Logger {
  private val url = new URL(source)
  private var lastModified: Long = 0
  private var data: Node = _

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

  def checkUpdate = try {
    url.openConnection match {
      case con: HttpURLConnection => {
        con.setRequestMethod("HEAD")
        (con.getLastModified > lastModified)
      }
      case _ => false
    }
  } catch {
    case _ => false
  }

  def updated = new Date(lastModified)

  def content: Option[Node] = try {
    url.openConnection match {
      case con: HttpURLConnection => {
        con.setRequestMethod("HEAD")

        if(con.getLastModified > lastModified) {
          lastModified = con.getLastModified
          data = XML.load(url)
        }

        if(data == null) {
          error("XML.load returns NULL")
          None
        } else {
          Some(data)
        }
      }
      case _ => error("Connection to '"+url.toString+"' couldn't be established!"); None
    }
  } catch {
    case e => error(e.toString); None
  }


}

}