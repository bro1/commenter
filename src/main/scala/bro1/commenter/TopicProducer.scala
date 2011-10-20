package bro1.commenter

import scala.xml._
import java.io.{File,FileReader,StringReader}
import java.text.{SimpleDateFormat}
import java.util.Date
import lj.scala.utils.HTMLTextUtils
import lj.scala.utils.TagSoupFactoryAdapter
import java.net.URL
import java.util.TimeZone
import bro1.commenter.impl.delfi._

object TopicProducerFactory {
  
  val producers = List(BernardinaiTopicProducer, DelfiTopicProducer)
  
  def getInstance(url : URL) = {
    producers.find(_.accepts(url))    
  }    
}

abstract class TopicProducer {
  
  def accepts(url : URL) : Boolean
  
  def matchesPattern(url : URL) : Option[URL] 
  
  def getTitle(url : URL) : String
  
  def getPoster() : Poster
 
  def getReader(url : String) : java.io.Reader = {
       
    if(url.startsWith("file://")) {
      
      val fileName = url.substring(7)      
      val file = FileLocationHelper.get(fileName)           
      new FileReader (file)

    } else {
      
      val content = lj.scala.utils.http.download(url)
      new StringReader (content)
      
    }
    
  }
  
  def process(url : String) : Topic = {
    // TODO: what to do about the ID
    val doc = new TagSoupFactoryAdapter load getReader(url)
    val topic = createTopic(-1L, url, doc)
    topic.comments = extractAllComments(doc)
    topic
  }
  
  
  /**
   * Add only the comments that do not exist in the topic
   * 
   * @return new comments
   */
  def processComments(topic : Topic) = {
    
    val doc = new TagSoupFactoryAdapter load getReader(topic.url)
    val allComments = extractAllComments(doc)
       
    val newComments = (allComments -- topic.comments)
    
    // add new comments to topic
    topic.comments ++= newComments
        
    // add new comments to the database
    if (!newComments.isEmpty) {      
       Data.saveComments(topic, newComments)
       topic.newComments = true
       
       //val s = MainApplication.topicsTable.selection       
       TopicModel.fireTableDataChanged
       //MainApplication.topicsTable.selection.cells.add(s.cells.first)
       
    }
    
    newComments
  } 

  def createTopic(id : Long, url : String, doc : Node) : Topic
  
  def extractAllComments(doc : Node) : List[Comment]
  
  def categoryName: String ;
}


  
object BernardinaiTopicProducer extends TopicProducer {
    
  def getPoster() : Poster = {
    // TODO: implement Bernardinai poster
		  null
  }
  
    def main(args: Array[String]) {
        // delfiLinks
        bernardinaiComments 
    }
    

    def accepts(url : URL) = {     
      (url.getProtocol() == "file" && url.toString.contains("bernardinai")) ||  matchesPattern(url).isDefined        
    }
    
    def matchesPattern(url : URL) : Option[URL] = {
      
      val urlPattern = """(http://www\.bernardinai\.lt/straipsnis/\d{4}-\d{2}-\d{2}-.*/(\d+))(/comments([#].*)?)?""".r
      
      url.toString() match { 
        case urlPattern(mainURL, articleID, comments, anchor) => Some(new URL(mainURL + "/comments"))
        case _ => None
      }       
    }
    
    @Override
    def getTitle(url : URL) = {
    	val doc = new TagSoupFactoryAdapter load getReader(url.toString())
    	val titleElement = (doc \\ "title").text
    	
    	val  toDrop = " - Bernardinai.lt"
    	
    	if (titleElement.endsWith(toDrop)) {
    		titleElement.dropRight(toDrop.length())
    	} else {
    	  titleElement
    	}
    }

    def bernardinaiComments = {
      
      process("file://" + "misc/examples/bernardinai/bernardinai2.html");
      
    }
    
    def createTopic(id : Long, url : String, doc: Node) = {
      
      /** TODO - get real title */
      val title = url
      
      new BernardinaiTopic(id, title, url)
    }
    
    
    def extractAllComments(doc : Node) : List[Comment] = {
      def filter(div : NodeSeq) = {
        (div \ "@class").text.startsWith("sf_comment ")
      }
      
      val coms = (doc \\ "div").filter(filter)
      coms.map(getComment).toList
    }

    def getComment(node : Node) = {
      new Comment( 
        -1,
        getID(node),
        getDate(node),
        getFrom(node),
        getCommentText(node))        
    }
    
    
    def getCommentText(node : Node) = {
        val coms = (node \\ "div").filter(div => (div \ "@class").text == "sf_comment_text")
        HTMLTextUtils.getText(coms.first)
    }
    
    def getID(node : Node) = {

        val nodeWithID = (node \ "@id").text
        val length = "sf_comment_".length
        nodeWithID.substring(length)
    }

    def getFrom (node : Node)  = {
        
        val coms = (node \\ "span").filter(span => (span \ "@class").text == "sf_comment_author")
        HTMLTextUtils.cleanUp(coms.first.text)
    }

  def getDateFromString(dateString : String) = {
     val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
     dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Vilnius"))
     dateFormat.parse(dateString)
  }

    def getDate(node : Node) = {
        val coms = (node \\ "span").filter(span => (span \ "@class").text == "comment_author_date")
        getDateFromString(coms.first.text)
    }

    def delfiLinks = {
        val url = new FileReader (new File ("misc/examples/delfi/str1p1.html"))
        val doc = new TagSoupFactoryAdapter load url
        download (urls(doc)) 
    }
   
    def download (strings : Seq[Node] ) {
        println (strings.map(s=> s.attribute("href").mkString))
    }
   
   def urls(doc : Node)  = 
      (doc \\ "a").filter(_.attribute("class").mkString == "comNav")
         
   def categoryName : String = "bernardinai"
  
}

