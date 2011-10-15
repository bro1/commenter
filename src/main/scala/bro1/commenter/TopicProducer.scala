package bro1.commenter

import scala.xml._
import java.io.{File,FileReader,StringReader}
import java.text.{SimpleDateFormat}
import java.util.Date
import lj.scala.utils.HTMLTextUtils
import lj.scala.utils.TagSoupFactoryAdapter
import java.net.URL

import java.util.TimeZone

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


object DelfiTopicProducer extends TopicProducer {

     def main(args: Array[String]) {
       val url = args{0}
       process(url)
     }
     
    def createTopic(id : Long, url : String, doc : Node) = {
      new DelfiTopic(id, url, url) 
    } 

    
    def extractAllComments(doc : Node) : List[Comment] = {
       val comments = (doc \\ "div").filter(div => {
           (div \ "@class").text == "comm-container" && div.attribute("id").isDefined
       	})
       
       comments.map(extractComment).toList
    }
     
    def extractComment (com : scala.xml.Node) = {
            
      new Comment(
         -1,
         getId(com),
         dt(com),
         getFrom(com),
         getCommentText(com))
     
   }
    
    
   def getCommentText (com : scala.xml.Node) = {

     val divs = (com \ "div")
     val commentDivs = divs.filter(div => (div \ "@class").text == "comm-text")
     val innerCommentDivs = commentDivs \ "div"
     val divsWithNoClass = innerCommentDivs.filter(div => (div \ "@class").isEmpty)     
  
     HTMLTextUtils.getText(divsWithNoClass.first)
   }    

    
   def getId(com : scala.xml.Node) = {
     val id : String = com.attribute("id").get.text
     id.substring(1)
   }

   
   /**
    * Delfi returns date in the following format 
    * 2009 09 13 10:48 
    */
   def dt(com : scala.xml.Node) = {
     val strDate =            
         ((com \ "div").filter(_.attribute("class").mkString == "comm-name") \ "div").text
     getDate(strDate)
   }

  def getDate(dateString : String) = {
     val dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm");       
     dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Vilnius"))
     dateFormat.parse(dateString)
  }
   
              
   def getFrom(com : scala.xml.Node) = {
      ((com \ "div").filter(_.attribute("class").mkString == "comm-name") \ "strong").text
   }
//    def accepts(url : URL) = {
//      url.getHost() == "www.delfi.lt"
//    }
   
   
    def accepts(url : URL) = {     
      (url.getProtocol() == "file" && url.toString.contains("delfi")) ||  matchesPattern(url).isDefined        
    }
    
    def matchesPattern(url : URL) : Option[URL] = {
      
      val urlPattern = """(http://(\w+)\.delfi\.lt/.*?id=(\d+))(&.*)?""".r
      
      url.toString() match { 
        case urlPattern(mainURL, domain3rdLevel, id, params) => Some(new URL(mainURL + "&com=1"))
        case _ => None
      }       
    }
   

   @Override
   def getTitle(url : URL) = {
    	val doc = new TagSoupFactoryAdapter load getReader(url.toString())
    	
    	val titleElement = (doc \\ "title").text.trim()
    	
    	val  toDrop = " - DELFI Žinios"
    	
    	if (titleElement.endsWith(toDrop)) {
    		titleElement.dropRight(toDrop.length())
    	} else {
    	  titleElement
    	}
   }
   
   def categoryName : String = "delfi"   
  
}


