package code.model

import _root_.code.mymashi.source.Source
import net.liftweb.mapper._
import com.sun.syndication.io.impl.DateParser
import java.util.Date

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 21.05.11
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */

class InformationSource extends KeyedMapper[String, InformationSource] {
  def getSingleton = InformationSource

  def primaryKeyField = url
  object url extends MappedStringIndex(this, 200) with IndexedField[String] {
    override def dbNotNull_? = true
    override def writePermission_? = true
    override def dbAutogenerated_? = false
  }
  object title extends MappedString(this, 300)
  object description extends MappedText(this)
  object updated extends MappedDateTime(this)
  object language extends MappedLocale(this)
  object image extends MappedString(this, 200)
  object cache extends MappedText(this)

  override def toString = "url: "+url+", title: "+title
}

object InformationSource extends InformationSource with KeyedMetaMapper[String, InformationSource]



