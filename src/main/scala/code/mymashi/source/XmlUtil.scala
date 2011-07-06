package code.mymashi.source

import _root_.code.mymashi.XmlUnescapeReader
import org.xml.sax.InputSource
import org.ccil.cowan.tagsoup._
import jaxp.SAXParserImpl
import xml.{Node, Utility, Text, XML}
import java.io.StringReader

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 02.06.11
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */

object XmlUtil {
  private val features = new java.util.HashMap[String, Boolean]
  val parser = SAXParserImpl.newInstance(features.asInstanceOf[java.util.HashMap[_, _]])

  def extractText(text: String): String = {
    val data = XML.loadXML(new InputSource(new XmlUnescapeReader(new StringReader(text))), parser)
    data.descendant.filter(_.isInstanceOf[Text]).mkString.trim
  }

  def unescape(text: String): String = {
    var tmp = text
    for((c, esc) <- Utility.Escapes.escMap) {
      tmp = tmp.replaceAll(esc, c.toString)
    }
    tmp
  }

}