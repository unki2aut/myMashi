package main.scala.code.mymashi.source

import org.xml.sax.InputSource
import java.io.{StringReader, StringWriter}
import xml.{Utility, Text, XML}
import org.xml.sax.InputSource
import org.ccil.cowan.tagsoup._

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 02.06.11
 * Time: 12:54
 * To change this template use File | Settings | File Templates.
 */

object HtmlUtil {
  def extractText(text: String): String = {
    val unsafe = unescape(text)
    val parser = new Parser
    val out = new StringWriter

    val source = new InputSource(new StringReader(unsafe))
    source.setEncoding("utf-8")

    val handler = new XMLWriter(out)
    handler.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes")

    parser.setContentHandler(handler)
    parser.parse(source)

    val esc = out.getBuffer.toString

    XML.loadString(esc).descendant.filter(_.isInstanceOf[Text]).mkString.trim
  }

  def unescape(text: String): String = {
    var tmp = text
    for((c, esc) <- Utility.Escapes.escMap) {
      tmp = tmp.replaceAll(esc, c.toString)
    }
    tmp
  }

}