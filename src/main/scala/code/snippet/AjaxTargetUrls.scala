package code.snippet

import _root_.code.mymashi.source.Item
import net.liftweb._
import common.{Logger, Full}
import http._
import util.Helpers._
import js._
import JsCmds._
import JE._
import js.jquery.JqJsCmds.{AppendHtml, FadeOut, Hide, FadeIn}
import scala.xml._
import xml.NodeSeq._
import java.util.Date
import scala.math._
import _root_.code.mymashi.{LuceneIndex, XmlSource}


case class Feed(var url: String, var keywords: String, var source: XmlSource = null) {
  val guid: String = nextFuncName

  override def toString = "url: "+url+", keywords: "+keywords+", exists: "+source.exists
}

// class -> new per request
// object -> new per restart and for all users
class AjaxTargetUrls extends Logger {
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
          if(u.length == 0) {
            Noop
          } else if(checkUrl(u, f)) {
            f.source.content match {
              case Some(c) => {
                LuceneIndex.indexSource(f)
                SetValById(f.guid+"_url", Str(f.url)) & SetHtml(f.guid+"_err", NodeSeq.Empty)
              }
              case None => SetValById(f.guid+"_url", Str(f.url)) & SetHtml(f.guid+"_err", Text("Couldn't load Source"))
            }
          } else {
            SetValById(f.guid+"_url", Str(f.url)) & SetHtml(f.guid+"_err", Text("URL not valid"))
          }
        },
        "id" -> (f.guid+"_url"), "size" -> "30")
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
    // TODO: this might not be save, check keywords
    urls.filter(_.source != null).flatMap(f => LuceneIndex.search(f))
      .sortWith((i1, i2) => sort(i1,i2)).flatMap(_.toHtml)
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

  private def sort(i1: Item, i2: Item): Boolean = {
    if(i1.updated == null || i2.updated == null) {
      warn("sort: updated is null")
      false
    } else {
      i1.updated.after(i2.updated)
    }
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



