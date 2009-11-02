import java.io.{File,FileReader,StringReader}
import java.text.{SimpleDateFormat}
import java.util.Date
import scala.xml._
//import lj.scala.utils.HTMLTextUtils
//import lj.scala.utils.TagSoupFactoryAdapter

import java.util.TimeZone


abstract class TopicProducer {
 
  def getReader(url : String) : java.io.Reader = {
    
    var reader : java.io.Reader = null;
    
    if(url.startsWith("file://")) {
      
      val fileName = url.substring(7)
      new FileReader (new File(fileName))

    } else {
      
      val content = lj.scala.utils.http.download(url)
      new StringReader (content)      
    }
    
  }
  
  def process(url : String) = {
    // TODO: what to do about the ID
    processTopic(-1L, url, new TagSoupFactoryAdapter load getReader(url))
  }
  
  def processComments(topic : Topic) = {
    val doc = new TagSoupFactoryAdapter load getReader(topic.url)
    extractComments(doc, topic)
  } 
  
  def processTopic(id : Long, url : String, doc : Node) : Topic
  
  def extractComments(doc : Node, topic : Topic) 
}


  
object BernardinaiTopicProducer extends TopicProducer {
    
    def main(args: Array[String]) {
        // delfiLinks
        bernardinaiComments 
    }


    def bernardinaiComments = {
      
      process("file://" + "misc/examples/bernardinai/bernardinai2.html");
      
    }
    
    def processTopic(id : Long, url : String, doc: Node) = {
      val topic = new BernardinaiTopic(id, url, url)
      extractComments(doc, topic)
      topic 
   }
    
    def extractComments(doc : Node, topic : Topic) = {
      val coms = (doc \\ "div").filter(_.attribute("class").mkString == "comment")
      coms foreach {(com) =>
        topic.comments  ::= cmt (com)        
      }  
      
    }
    

    def cmt(node : Node) = {
      new Comment( 
        -1,
        getID(node),
        getDate(node),
        getFrom(node),
        getCommentText(node))        
    }
    
    
    def getCommentText(node : Node) = {
        val coms = (node \\ "div").filter(_.attribute("class").mkString == "text")
        HTMLTextUtils.processChildren(coms.first)        
    }
    
    def getID(node : Node) = {
      val coms = (node \\ "span").filter(_.attribute("class").mkString == "report-inappropriate-comment")
      val aElement = coms \ "a"
      if (aElement.length != 0) {        
        val value = aElement.first.attribute("onclick")        
        val onClickValue = value.get.text
        val r = """return reportInappropriateComment\((\d+)\)""".r
        val r(theID) = onClickValue
        theID
      } else {
        "unknown"
      }
    }

    def getFrom (node : Node)  = {
        val coms = (node \\ "span").filter(_.attribute("class").mkString == "author")
        HTMLTextUtils.cleanUp(coms.first.text)
    }

  def getDateFromString(dateString : String) = {
     val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
     dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Vilnius"))
     dateFormat.parse(dateString)
  }

    def getDate(node : Node) = {
        val coms = (node \\ "span").filter(_.attribute("class").mkString == "time date")
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
  
}


object DelfiTopicProducer extends TopicProducer {

     def main(args: Array[String]) {
       val url = args{0}
       process(url)
     }
     
    def processTopic(id : Long, url : String, doc : Node) = {
                   
       val topic = new DelfiTopic(id, url, url) 
      
       extractComments(doc, topic)
       
       topic
    } 
    
    def extractComments(doc : Node, topic : Topic) = {
      
       val comments = (doc \\ "div").filter( _ \ "@class" == "comm-container")
       
       comments foreach {(com) =>
         topic.comments ::= extractComment(com)
       }  
      
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
      
       println("----------------------------")
      
       val n = ((com \ "div").filter(
           _ \ "@class" == "comm-text") \ "div").filter( _ \ "@class" != Some)
  
       val commentText = HTMLTextUtils.processChildren(n.first)
       println (commentText)
       commentText
      
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
  
}
