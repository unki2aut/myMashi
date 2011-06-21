package code.mymashi.classify

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 16.06.11
 * Time: 21:22
 * To change this template use File | Settings | File Templates.
 */

class TrainingSet(val user: String, val instances: collection.mutable.Set[Instance]) {
  def size: Double = instances.foldLeft(0.0)((b, s) => b + s.size)

  def add(i: Instance) {
    instances + i
  }
}