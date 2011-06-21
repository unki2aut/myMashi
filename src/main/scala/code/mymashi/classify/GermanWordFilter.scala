package code.mymashi.classify

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 19.06.11
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */

object GermanWordFilter {
  //val words = collection.immutable.HashSet(
  val words = Seq(
    // conjunctions
    "und", "weil", "deshalb", "darum", "deswegen", "aber", "denn", "sondern", "als",
    "wenn", "sobald", "sodass", "dass",
    // articles
    "der", "die", "das", "dem", "den", "des", "ein", "eine", "einer", "eines", "einem", "einen",
    // pronoun
    "ich", "meiner", "mir", "mich", "du", "deiner", "dir", "dich", "er", "seiner", "ihm", "ihn", "sie",
    "ihrer", "ihr", "es", "wir", "unser", "uns", "euer", "euch", "ihnen", "wer", "was", "welcher",
    // adverb
    "wo", "wann", "wie", "warum", "weshalb", "wieso",
    // preposition
    "durch", "für", "gegen", "ohne", "um", "zu", "wegen", "bei", "trotz", "vor", "nach", "ab", "aus", "von",
    "an", "auf", "außer", "gegenüber", "hinter", "in", "neben", "über", "unter", "zwischen", "außerhalb",
    "diesseits", "entlang", "inmitten", "innerhalb", "jenseits", "längs", "oberhalb", "unterhalb", "unweit", "mit",
    "angesichts", "anlässlich", "aufgrund", "betreffs", "bezüglich", "dank", "gemäß", "halber", "infolge", "kraft",
    "laut", "mangels", "mittels", "ob", "seitens", "unbeschadet", "ungeachtet",
    "vermittels", "vermöge", "zufolge", "zwecks", "abzüglich", "ausschließlich", "bis", "einschließlich",
    "entgegen", "exklusive", "inklusive", "mitsamt", "nebst", "samt", "statt", "wider",
    "zuwider", "zuzüglich",
    // conjunction
    "damit", "da", "falls", "obwohl", "bevor", "nachdem"
    )

  def filter(list: List[String]): List[String] = {
    list.filterNot(words.contains)
  }
}