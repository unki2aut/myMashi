package code.myashi

import _root_.code.mymashi.XmlSource
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.{Before, Test}
import xml.Node
import net.liftweb.common.Full

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 21.05.11
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */

class XmlSourceTest extends AssertionsForJUnit {
  private var source: XmlSource = _

  @Before
  def initSource {
    source = new XmlSource("http://www.rssboard.org/files/sample-rss-2.xml")
  }

  @Test
  def existsTest {
    assertTrue("sample-rss-2.xml not found!", source.exists)
  }

  @Test
  def checkUpdateTest {
    assertTrue(source.checkUpdate)
    assertFalse(source.content.isEmpty)
    assertFalse(source.checkUpdate)
  }

  @Test
  def checkContent {
    source.content match {
      case Some(n) => {
        assertTrue(n.label.equals("rss"))
        assertTrue((n \\ "item").size == 4)
      }
      case _ => fail()
    }
  }
}