package code.mymashi

import java.io.{Reader, BufferedReader}
import java.lang.String
import xml.Utility

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 22.06.11
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */

class XmlUnescapeReader(reader: Reader) extends Reader(reader) {
  private val buf = new StringBuffer
  private val in = new BufferedReader(reader)
  private var l = in.readLine
  private var index = 0

  while(l != null) {
    buf.append(unescape(l))
    l = in.readLine
  }

  private val data = buf.toString.toCharArray

  def unescape(text: String): String = {
    var tmp = text
    for((c, esc) <- Utility.Escapes.escMap) {
      tmp = tmp.replaceAll(esc, c.toString)
    }
    tmp
  }

  override def read: Int = {
    lock synchronized {
      if(index < data.length) {
        index += 1
        data.charAt(index-1)
      } else {
        -1
      }
    }
  }

  override def read(cbuf: Array[Char], off: Int, len: Int): Int = {
    lock synchronized {
      if(index == data.length) {
        return -1
      } else if ((off < 0) || (off > cbuf.length) || (len < 0) ||
        ((off + len) > cbuf.length) || ((off + len) < 0)) {
          throw new IndexOutOfBoundsException
      } else if (len == 0) {
        return 0
      }

      val n = len.min(data.length - index)

      /*
        src the source array.
        srcPos starting position in the source array.
        dest the destination array.
        destPos starting position in the destination data.
        length the number of array elements to be copied.
       */
      System.arraycopy(data, index, cbuf, off, n)

      index += n

      n
    }
  }

  override def close {
    if(reader == null) return

    in.close
  }
}