package code {
package snippet {

import net.liftweb._
import http._
import js.JsCmds.SetValById._
import util.Helpers._
import js._
import JsCmds._
import JE._
import js.jquery.JqJsCmds.{AppendHtml, FadeOut, Hide, FadeIn}
import scala.xml._
import at.tuwien.mymashi._
import java.net.URL
import java.text.SimpleDateFormat
import xml.NodeSeq._
import java.util.Date
import com.sun.syndication.io.impl.DateParser
import net.liftweb.util.TimeHelpers
import scala.math._


final case class Feed(var url: String, var keywords: String, var source: XmlSource = null,
                      guid: String = nextFuncName, var date: Date = new Date())

case class Article(title: String, description: String, date: Date, favicon: String)

// class -> new per request
// object -> new per restart and for all users
class AjaxTargetUrls {
  private var urls = Vector(new Feed("", ""))

  def render(html: NodeSeq): NodeSeq = {
    <div id="feeds">
      { urls.flatMap(doFeed) }
    </div> ++
    renderAdd(("#feeds ^^" #> "^^")(html)) ++
    SHtml.ajaxButton("update", () => {
      SetHtml("results", renderResults)
    }) ++
    <div id="results" style="margin-top:30px" />
  }

  private def renderAdd(node: NodeSeq) = {
    SHtml.ajaxButton("+", () => {
      val feed = new Feed("", "")
      urls :+= feed
      AppendHtml("feeds", doFeed(feed)) &
        Hide(feed.guid) &
        FadeIn(feed.guid, TimeSpan(0), TimeSpan(500))
    })
  }

  private def doFeed(f: Feed) = {
    <div id={f.guid}>
      url: {
        SHtml.ajaxText(f.url, u => {
          if(checkUrl(u, f)) {
            SetValById(f.guid+"_url", Str(f.url)) & SetHtml(f.guid+"_err", NodeSeq.Empty)
          } else {
            SetValById(f.guid+"_url", Str(f.url)) & SetHtml(f.guid+"_err", Text("URL not valid"))
          }
        },
        "id" -> (f.guid+"_url"), "size" -> "50")
      }
      keywords: {
        SHtml.ajaxText(f.keywords, k => {
          f.keywords = k;
          Noop
        }) ++
        SHtml.ajaxButton("-", () => {
          urls = urls.filterNot(_.guid == f.guid)
          FadeOut(f.guid, TimeSpan(0), TimeSpan(500)) &
            After(TimeSpan(500), Replace(f.guid, NodeSeq.Empty))
        })
      }
      <span id={f.guid+"_err"} style="color:red" />
    </div>
  }

  private def renderResults(): NodeSeq = {
    // show the result
    urls.filter(_.source != null).flatMap(c => {
      val kw = tokenizeKeywords(c.keywords.toLowerCase)
      convertFeed(c.source.content).filter(filterKeywords(_, kw))
    }).sortWith((a1, a2) => a1.date.compareTo(a2.date) > 0).flatMap(renderArticle)
  }

  private def convertFeed(content: NodeSeq): Seq[Article] = {
    val root = content.head

    (root, root.label) match {
      case (rss, "rss") => convertRss(rss)
      case (atom, "feed") => convertAtom(atom)
      case (html, "html") => Seq(new Article("HTML page found", trimHtml(html), TimeHelpers.now, "images/news.ico"))
      case (other, _) => Seq(new Article("Different source", trimHtml(other), TimeHelpers.now, "images/news.ico"))
    }
  }

  private def renderArticle(article: Article): NodeSeq = {
    <div>
      <strong><img src={article.favicon} /> {article.title} on {fromDate(article.date)}</strong>
      <p>{article.description}</p>
    </div>
  }

  private def filterKeywords(article: Article, kw: List[String]): Boolean = {
     kw.exists((article.title+" "+article.description).toLowerCase.contains)
  }

  private def tokenizeKeywords(kw: String): List[String] = {
    kw.split("""\s+""").toList
  }

  private def convertRss(c: NodeSeq): Seq[Article] = {
    val title = c \ "title" text

    c \\ "item"  map(item => {
      val iTitle = mergeTitle(title, item \ "title" text)
      new Article(iTitle, item \ "description" text, DateParser.parseDate(item \ "pubDate" text), "images/news.ico")
    })
  }

  private def convertAtom(c: NodeSeq): Seq[Article] = {
    val title = c \ "title" text

    c \\ "entry"  map(item => {
      val iTitle = mergeTitle(title, item \ "title" text)
      new Article(iTitle, item \ "summary" text, DateParser.parseDate(item \ "updated" text), "images/news.ico")
    })
  }

  private def checkUrl(url: String, feed: Feed): Boolean = {
    if(url.equals("")) return true

    feed.url = url

    if(!url.startsWith("http://")) {
      feed.url = "http://"+url
    }

    feed.source = new XmlSource(feed.url)
    feed.source.exists
  }

  private def fromDate(date: Date): String = {
    new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date)
  }

  private def mergeTitle(t1: String, t2: String): String = {
    if(t2.compareTo("") == 0) t1
    else if(t1.compareTo("") == 0) t2
    else t1 + " - " + t2
  }

  private def trimHtml(node: NodeSeq): String = {
    val tmp = node.mkString
    tmp.substring(0, min(tmp.length, 500))
  }
}

}
}


