package bro1.commenter.impl.delfi

import java.lang.Override
import java.net.URL
import java.text.SimpleDateFormat

import scala.xml.Node

import bro1.commenter.Comment
import bro1.commenter.DelfiTopic
import bro1.commenter.Poster
import bro1.commenter.TopicProducer
import lj.scala.utils.HTMLTextUtils
import lj.scala.utils.TagSoupFactoryAdapter

object DelfiTopicProducer extends TopicProducer {

  def getPoster() : Poster = {
		  DelfiPoster
  }  
  
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

