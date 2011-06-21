package code.mymashi.classify

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 16.06.11
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */

import scala.collection.mutable.Map

// TODO: improve p by field context

class NaiveBayes(val tSet: TrainingSet) {
  val like = Map.empty[String, Double]
  val dislike = Map.empty[String, Double]
  tSet.instances.foreach(learn)

  private def learn(i: Instance) {
    val freq = if(i.like) like else dislike

    i.freq.foreach {
      case (word, count) => freq.get(word) match {
        case Some(value) => freq.put(word, value + count)
        case None => freq.put(word, count)
      }
    }
  }

  def train(instance: Instance) {
    tSet.instances + instance
    learn(instance)
  }

  def classify(i: Instance): Double = {
    var list = List.empty[Double]

    i.freq.keys.foreach(word => {
      val l = 2 * (like.get(word) orElse Some(0.0) get)
      val d = dislike.get(word) orElse Some(0.0) get

      if(d == 0 && l != 0) { // in like only
        list = (if(l > 10) 0.99 else 0.9) :: list
      } else if(l == 0 && d != 0) { // in dislike only
        list = (if(d > 10) 0.01 else 0.1) :: list
      } else if (l+d > 5) { // only if relevant enough
        val like_factor = l / like.size
        val dislike_factor = d / dislike.size
        val p = like_factor / (like_factor + dislike_factor)
        list = p :: list
      }
    })

    val p_list = list.sorted.reverse.take(15)

    //println("classify - p_list: "+p_list)

    NaiveBayes.combineProbabilities(p_list)
  }
}

object NaiveBayes {
  def combineProbabilities(p_list: List[Double]): Double = {
    if(p_list.isEmpty) return 0.01

    p_list.product / (p_list.product + p_list.map(1.0 - _).product) match {
      case x if x < 0.01 => 0.01
      case x => x
    }
  }
}