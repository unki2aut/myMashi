package code.mymashi.classify

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 19.06.11
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */

class Instance(val label: String, val like: Boolean, private val fields: collection.Map[String, List[String]]) {
  override def equals(that: Any): Boolean = {
    val t = that.asInstanceOf[Instance]
    label.equals(t.label) &&
      fields.forall(m => t.fields.get(m._1) match {
        case Some(s) => m._2.contains(s)
        case None => false
      })
  }

  def freq: collection.mutable.Map[String, Double] = {
    val f = collection.mutable.Map.empty[String, Double]

    fields.foreach {
      case (field, words) => words.foreach(word => {
        f.get(word) match {
          case Some(value) => f.put(word, value + 1.0)
          case None => f.put(word, 1.0)
        }
      })
    }

    f
  }

  def size: Double = fields.values.foldLeft(0.0)((b, s) => b + s.size)

  override def toString: String = {
    label+" - "+fields
  }
}
