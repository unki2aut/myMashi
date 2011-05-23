package code.mymashi.source

import _root_.code.model.InformationSource
import java.util.Date
import java.text.SimpleDateFormat
import com.sun.syndication.io.impl.DateParser
import net.liftweb.util.TimeHelpers
import xml._
import net.liftweb.common.{Full, Box}

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 22.05.11
 * Time: 08:35
 * To change this template use File | Settings | File Templates.
 */

abstract class Source {
  def listItems: Seq[Item]

  val url: String
  val title: String
  val updated: Date
  val link: String
  val description: String
  val language: String
  val image: String
  val cache: NodeSeq

  def insertDb {
    val infoSrc = InformationSource.create
    infoSrc.url(url)
    infoSrc.title(title)
    infoSrc.description(description)
    infoSrc.updated(updated)
    infoSrc.language(language)
    infoSrc.image(image)
    infoSrc.cache(cache.mkString)
    infoSrc.save
  }
}

object Source {
  def toSource(url: String, node: Option[Node]): Option[Source] = {
    node match {
      case Some(root) => (root, root.label, root.scope.uri) match {
        case (html, "html", _) => None // TODO:
        case (rss, "rss", _) => Some(new RssSource(url, root))
        case (rss, _, uri) if uri.startsWith("http://purl.org/rss/") => Some(new RssSource(url, root))
        case (rss, "RDF", _) if (rss \ "channel").nonEmpty => Some(new RssSource(url, root))
        case (atom, "feed", _) => Some(new AtomSource(url, root))
        case (other, _, _) => None
      }
      case _ => None
    }
  }

  def toImage(node: NodeSeq): String = if(node nonEmpty) node text else "images/news.ico"
  def toDate(node: NodeSeq): Date = if(node nonEmpty) DateParser.parseDate(node text) else TimeHelpers.now
  def filterItems(node: NodeSeq, itemLabel: String): NodeSeq = node.head match {
    case e: Elem => {
      // TODO: nicht sehr sauber!!
      e.copy(child = node.head.child.filterNot(x => x.label.equals(itemLabel) || x.isInstanceOf[Text]))
    }
    case _ => NodeSeq.Empty
  }
}

class Item(val url: String,
           val guid: String,
           val title: String,
           val link: String,
           val content: String,
           val updated: Date,
           val image: String,
           val cache: NodeSeq,
           val score: Float = 0) {

  def toHtml: Node = {
    <div>
      <strong>
          <img src={image} height="16px" style="margin-left:5px; vertical-align:middle" />
          <a href={link} target="_blank">{title}</a> on {formatDate} ({score})
      </strong>
      <p>{content}</p>
    </div>
  }

  override def toString = "title: "+title+", updated: "+storeDate+", url: "+url

  private def formatDate: String = {
    if(updated == null) {
      ""
    } else {
      new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(updated)
    }
  }

  def storeDate: String = {
    if(updated == null) {
      ""
    } else {
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(updated)
    }
  }
}
