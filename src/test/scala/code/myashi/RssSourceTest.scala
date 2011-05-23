package code.mymashi

import _root_.code.myashi._
import _root_.code.mymashi.source._
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.{Test, Before}
import xml.{Node, XML, NodeSeq}
import com.sun.syndication.io.impl.DateParser
import net.liftweb.util.TimeHelpers

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 22.05.11
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */

class RssSourceTest extends AssertionsForJUnit {

  @Test
  def rssSourceTest {
    Source.toSource(TestData.url, Some(TestData.node)) match {
      case Some(rss: RssSource) => {
        assertEquals(TestData.url, rss.url)
        assertEquals("Liftoff News", rss.title)
        assertEquals(DateParser.parseDate("Tue, 10 Jun 2003 04:00:00 GMT"), rss.updated)
        assertEquals("http://liftoff.msfc.nasa.gov/", rss.link)
        assertEquals("Liftoff to Space Exploration.", rss.description)
        assertEquals("en-us", rss.language)
        assertEquals("images/news.ico", rss.image)
        assertEquals(TestData.channel, rss.cache)

        assertEquals(3, rss.listItems.size)
      }
      case _ => fail("source is no RSS-Source!")
    }
  }

  @Test
  def rssItemTest {
    Source.toSource(TestData.url, Some(TestData.node)) match {
      case Some(rss: RssSource) => {
        for(item <- rss.listItems) {
          assertTrue(item.guid.startsWith("http://liftoff.msfc.nasa.gov/"))
          assertTrue(item.title.nonEmpty)
          assertTrue(item.link.startsWith("http://liftoff.msfc.nasa.gov/news/2003/news-"))
          assertTrue(item.content.nonEmpty)
          assertEquals(2003, TimeHelpers.year(item.updated))
          assertEquals("images/news.ico", item.image)
          assertTrue(item.cache.ne(NodeSeq.Empty))
        }
      }
      case _ => fail("source is no RSS-Source!")
    }
  }
}