package code.mymashi

import _root_.code.model.InformationSource
import _root_.code.snippet.Feed
import _root_.code.mymashi.source._
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Field, Document}
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search._
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.apache.lucene.util.SetOnce.AlreadySetException
import com.sun.syndication.io.impl.DateParser
import net.liftweb.mapper._
import net.liftweb.common._
import java.io.{IOException, File}
import xml.{XML, NodeSeq}
import org.apache.lucene.index.{Term, IndexWriterConfig, IndexWriter}
import net.liftweb.util.TimeHelpers
import java.util.Date

/**
 * Created by IntelliJ IDEA.
 * User: unki2aut
 * Date: 20.05.11
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */

object LuceneIndex extends Logger {
  private val indexFile = new File(System.getProperty("user.dir")+"/lucene-index")
  private val indexDir = FSDirectory.open(indexFile)
  private val analyzer = new StandardAnalyzer(Version.LUCENE_31)

  def indexSource(feed: Feed) {
    try {
      val iw = new IndexWriter(indexDir, new IndexWriterConfig(Version.LUCENE_31, analyzer).
                    setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND))

      iw.maybeMerge

      info("index feed: "+feed)

      InformationSource.find(By(InformationSource.url, feed.url)) match {
        case Full(infoSrc) => {
          info("found feed: "+infoSrc.url+"")

          info("feed.updated: "+feed.source.updated+", db.updated: "+infoSrc.updated.get)

          if (feed.source.updated.after(infoSrc.updated.get)) {
            // store only new items
            Source.toSource(feed.url, feed.source.content) match {
              case Some(src) =>  {
                src.listItems.filter(_.updated.after(infoSrc.updated.get)).
                  foreach(x => iw.addDocument(infoToLuceneDoc(x), analyzer))

                // update updated in DB
                infoSrc.updated(src.updated)
                infoSrc.save
              }
              case None => Unit
            }
          } else {
            info("feed is up to date")
          }
        }
        case Empty => {
          // source not found -> store it
          Source.toSource(feed.url, feed.source.content) match {
            case Some(src) => {
              src.insertDb
              info("insert into DB")

              src.listItems.foreach(x => iw.addDocument(infoToLuceneDoc(x), analyzer))
            }
            case None => info("content was no valid source"); Unit // no source found
          }
        }
        case _ => info("some problem with finding the feed url"); Unit // source found, but nothing to update
      }

      iw.commit
      iw.close
    } catch {
      case ioX: IOException => error("IOException: "+ioX.getMessage)
      case asX: AlreadySetException => error("AlreadySetException: "+asX.getMessage)
      case e: Exception => {
        error("Other Exception: "+e.getMessage)
        e.getStackTrace.filter(p => p.getClassName.startsWith("code.mymashi") ||
          p.getClassName.startsWith("org.apache.lucene")).foreach(error(_))
      }
    }
  }

  def search(feed: Feed): Seq[Item] = {
    try {
      val is = new IndexSearcher(indexDir, true)

      val query = new BooleanQuery()

      query.add(new TermQuery(new Term("url", feed.url)), BooleanClause.Occur.MUST)

      if(feed.keywords.size > 0) {
        query.add(new QueryParser(Version.LUCENE_31, "title", analyzer).parse(feed.keywords), BooleanClause.Occur.SHOULD)
        query.add(new QueryParser(Version.LUCENE_31, "content", analyzer).parse(feed.keywords), BooleanClause.Occur.SHOULD)
      }

      info("searching for: "+feed.url+" "+feed.keywords)

      val hits = is.search(query, 10).scoreDocs;

      info("found "+hits.length+" hits")

      val result = for(scoreDoc <- hits;
                    doc = is.doc(scoreDoc.doc)) yield {

        new Item(doc.get("url"),
          doc.get("guid"),
          doc.get("title"),
          doc.get("link"),
          doc.get("content"),
          DateParser.parseDate(doc.get("updated")),
          doc.get("image"),
          NodeSeq.Empty,
          scoreDoc.score)
      }

      is.close

      result.filter(_.score > 1.0)
    } catch {
      case ioX: IOException => error(ioX.getMessage); Seq()
      case asX: AlreadySetException => error(asX.getMessage); Seq()
    }
  }

  private def infoToLuceneDoc(item: Item): Document = {
    val doc = new Document

    info("index doc: "+item)

    doc.add(new Field("url", item.url, Field.Store.YES, Field.Index.NOT_ANALYZED))
    doc.add(new Field("guid", item.guid, Field.Store.YES, Field.Index.NO))
    doc.add(new Field("title", item.title, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("content", item.content, Field.Store.YES, Field.Index.ANALYZED))
    doc.add(new Field("link", item.link, Field.Store.YES, Field.Index.NO))
    doc.add(new Field("updated", item.storeDate, Field.Store.YES, Field.Index.NO))
    doc.add(new Field("image", item.image, Field.Store.YES, Field.Index.NO))

    doc
  }

}
