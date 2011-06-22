package code.myashi

import code.mymashi.classify._
import org.scalatest.junit.AssertionsForJUnit
import org.junit.Assert._
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.util.Version
import java.io.StringReader
import scala.collection._
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 21.05.11
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */

class NaiveBayesTest extends AssertionsForJUnit {
  val analyzer = new StandardAnalyzer(Version.LUCENE_31)

  val t1 = new Instance("t1", true, Map("title" -> tokenize("HTC Desire: Bekommt doch Gingerbread [UPDATE]"),
    "content" -> tokenize("Verwirrspiel bei HTC: Erst hieß es, Besitzer des Handys HTC Desire müssten auf das Update Android 2.3 alias Gingerbread verzichten, jetzt soll es wohl doch kommen.")))

  val t2 = new Instance("t2", true, Map("title" -> tokenize("Discovr Apps: iOS-Programme finden leicht gemacht"),
    "content" -> tokenize("Wie soll man unter mittlerweile rund einer halben Million Apps für Apples iPhone, iPad und iPod touch noch den Überblick behalten, welche der kleinen Programme sich wirklich lohnen? Das Tool 'Discovr Apps' will genau diese Frage für sie beantworten.")))

  val t3 = new Instance("t3", false, Map("title" -> tokenize("Patchday: 16 Updates für Windows, Office und IE9"),
    "content" -> tokenize("Wie angekündigt hat Microsoft mal wieder seinen monatlichen Patchday veranstaltet. Dieses Mal wurden 16 Updates veröffentlicht, darunter auch die Internet Explorer Version 9.0.1.")))

  val t4 = new Instance("t4", true, Map("title" -> tokenize("LG Optimus Speed: Android 2.3 erst im Herbst?"),
    "content" -> tokenize("Für das Dual-Core-Handy LG P990 Optimus Speed steht das OS-Update auf 2.3 (Gingerbread) weiterhin aus. Und die Wartezeit auf die Neuerungen wird sich voraussichtlich verlängern.")))

  val t5 = new Instance("t5", false, Map("title" -> tokenize("Intel 700er-Serie: SSDs mit bis zu 2.200 MByte/s"),
    "content" -> tokenize("Zu Intels geplanter 700er-SSD-Reihe für Unternehmen sind weitere Infos aufgetaucht. Während die SSDs aus der Reihe 710 über SATA 2.0 angebunden werden, sollen die 720er-SSDs über eine PCIe-Schnittstelle am Mainboard betrieben werden und blitzschnelle Transferraten von bis zu 2.200 MByte in der Sekunde erreichen.")))

  val t6 = new Instance("t6", false, Map("title" -> tokenize("BlackBerry PlayBook: Ab heute erhältlich"),
    "content" -> tokenize("Mit dem heutigen 14. Juni betritt ein weiterer Wettbewerber die Tablet-Bühne: RIM schickt sein BlackBerry PlayBook mit selbstentwickeltem Betriebssystem gegen die iOS- und Android-Konkurrenz ins Rennen.")))

  val t7 = new Instance("t7", false, Map("title" -> tokenize("Firefox 5: Beta 6 steht zum Download bereit"),
    "content" -> tokenize("Mozilla feuert im Wochentakt neue Betas von Firefox 5 unters Volk. Taufrisch ist Firefox 5 Beta 6 in deutscher Sprache.")))

  val like = new Instance("like", true, Map("title" -> tokenize("HTC Desire: Bekommt kein Gingerbread-Update"),
    "content" -> tokenize("Besitzer des Handys HTC Desire müssen jetzt doch auf das Update Android 2.3 alias Gingerbread verzichten.")))

  val dislike = new Instance("dislike", true, Map("title" -> tokenize("SeaMonkey: Neue Version mit Firefox-4-Technik"),
    "content" -> tokenize("Der Softwarehersteller Mozilla hat sein Programm SeaMonkey, das aus einem Browser, einem Mail-Client, Webeditor sowie Chatprogramm besteht, in der Version 2.1 veröffentlicht. Die wohl wichtigste Neuerung: Der Browser ist auf dem Stand des aktuellen Firefox 4.")))

  val ok = new Instance("ok", true, Map("title" -> tokenize("HTC Sense 3.0: Jetzt doch für Handys mit Sense 2.x"),
    "content" -> tokenize("Entgegen seiner bisherigen Aussage bringt HTC die neuste Version seiner Handy-Nutzeroberfläche, Sense 3.0, nun doch für ausgewählte Android-Smartphones der Sense-2-Generation.")))


  val tSet = new TrainingSet("test", mutable.Set(t1, t2, t3, t4, t5, t6, t7))


  def tokenize(text: String): List[String] = {
    val tStream = analyzer.tokenStream("contents", new StringReader(text))
    val term = tStream.addAttribute[CharTermAttribute](classOf[CharTermAttribute])
    var ret = List[String]()

    while(tStream.incrementToken) {
      ret = ret ::: List(new String(term.buffer, 0, term.length))
    }

    GermanWordFilter.filter(ret)
  }

  @Test
  def classifySet {
    val nb = new NaiveBayes(tSet)

    println("t1: "+nb.classify(t1))
    //assertTrue(nb.classify(t1) > 0.9)

    println("like: "+nb.classify(like))
    //assertTrue(nb.classify(like) > 0.9)

    println("dislike: "+nb.classify(dislike))
    //assertTrue(nb.classify(dislike) < 0.1)

    println("ok: "+nb.classify(ok))
    //assertTrue(nb.classify(ok) > 0.5)
  }


}