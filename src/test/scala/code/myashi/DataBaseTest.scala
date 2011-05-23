package code.myashi

import _root_.bootstrap.liftweb.Boot
import _root_.code.model.InformationSource
import _root_.code.mymashi.source.{RssSource, Source}
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.junit.{Before, Test}
import net.liftweb.mapper.By
import net.liftweb.common.{Full, Empty}

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 21.05.11
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */

class DataBaseTest extends AssertionsForJUnit {
  @Test
  def createTableTest {
    try {
      val b = new Boot()
      // Boot your project
      b.boot
    } catch {
      case e: Exception => fail("couldn't boot: "+e.getMessage)
    }
  }

  @Test
  def inserUrlTest {
    Source.toSource(TestData.url, Some(TestData.node)) match {
      case Some(s: Source) => s.insertDb
      case None => fail("source is no RSS-Source!")
    }
  }

  @Test
  def findUrlTest {
    Source.toSource(TestData.url, Some(TestData.node)) match {
      case Some(rs) => InformationSource.find(By(InformationSource.url, TestData.url)) match {
        case Full(is: InformationSource) => assertEquals(TestData.url, is.url.get)
        case _ => fail("source not found!")
      }
      case None => fail("source is no RSS-Source!")
    }
  }

  @Test
  def deleteUrlTest {
    Source.toSource(TestData.url, Some(TestData.node)) match {
      case Some(rs) => InformationSource.find(By(InformationSource.url, TestData.url)) match {
        case Full(is: InformationSource) => is.delete_!
        case _ => fail("source not found!")
      }
      case None => fail("source is no RSS-Source!")
    }
  }
}