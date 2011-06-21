package code.mymashi.source {

import java.util.Date
import com.sun.syndication.io.impl.DateParser
import xml.{NodeSeq, Elem, Node}

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 22.05.11
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */

case class AtomSource(val url: String, private val node: Node, val lastModified: Long) extends Source {
  def listItems: Seq[Item] = (node \ "entry").map(new AtomItem(_, this))

  val title: String = node \ "title" text
  val updated: Date =  Source.toDate(node \ "updated", lastModified)
  val link: String = node \ "link" \ "@href" mkString
  val description: String = if(node \ "description" nonEmpty) node \ "description" text else title
  val language: String = if(node \ "language" nonEmpty) node \ "language" text else "en-EN"
  val image: String = Source.toImage(node \ "icon")
  val cache: NodeSeq = Source.filterItems(node, "entry")
}

case class AtomItem(node: Node, source: AtomSource)
  extends Item(source.url,
        node \ "id" text,
        node \ "title" text,
        node \ "link" \ "@href" mkString,
        if(node \ "content" isEmpty) node \ "summary" mkString else node \ "content" mkString,
        Source.toDate(node \ "updated", source.lastModified),
        source.image,
        node)

}