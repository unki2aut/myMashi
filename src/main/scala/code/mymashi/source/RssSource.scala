package code.mymashi.source

import java.util.Date
import xml.{NodeSeq, Node}

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 22.05.11
 * Time: 08:37
 * To change this template use File | Settings | File Templates.
 */

case class RssSource(val url: String, private val node: Node, val lastModified: Long) extends Source {
  private val channel = node \ "channel"

  def listItems: Seq[Item] = (channel\ "item").map(new RssItem(_, this))

  val title: String = channel \ "title" text
  val updated: Date =  Source.toDate(channel \ "pubDate", lastModified)
  val link: String = channel \ "link" text
  val description: String = channel \ "description" text
  val language: String = channel \ "language" text
  val image: String = Source.toImage(channel \ "image" \ "url")
  val cache: NodeSeq = Source.filterItems(channel, "item")
}

case class RssItem(node: Node, source: RssSource)
  extends Item(source.url,
      node \ "guid" text,
      node \ "title" text,
      node \ "link" text,
      node \ "description" text,
      Source.toDate(node \ "pubDate", source.lastModified),
      source.image,
      node)
