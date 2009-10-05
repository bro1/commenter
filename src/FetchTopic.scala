import java.text.{SimpleDateFormat}
import java.util.Date
import java.io.{StringReader,File,FileReader}
import scala.xml.Node

abstract  class TopicProducer {
  
  def process(url : String) = {
    
    var reader : java.io.Reader = null;
    
    if(url.startsWith("file://")) {
      reader = new FileReader (new File("misc/examples/bernardinai/bernardinai2.html"))

    } else {
      val content = lj.scala.utils.http.download(url)
      reader = new StringReader (content)             
    }
    
    processTopic(url, new TagSoupFactoryAdapter load reader)
  } 
  
  def processTopic(url : String, doc : Node) : Topic  
}

object HTMLTextUtils {
      def processChildren(com : Node) : String = {
      var text = ""      
      com.child foreach { (childElement) =>  text += convertNodeToText(childElement)}       
      text
    }
    
    
    def convertNodeToText(com : Node) : String = {
         
     com.label match {
       case "#PCDATA" => cleanUp(com.text)
       case "br" =>  "\n"
       case "i" => "_" + processChildren(com) +  "_"
       case "font" => "*" + processChildren(com) + "*" 
       case _ => processChildren(com)
     }      
      
    }
    
    def cleanUp(inputString : String) = {
      var result = inputString.replaceAll("&quot", "\"")
      result = result.replaceAll("&gt;", ">")
      result = result.replaceAll("&amp;", "&")
      
      if (result.startsWith("\n")) {
        result = result.substring(1)  
      }
      
      result
    } 

  
}

object FetchTopic extends TopicProducer {

     def main(args: Array[String]) {
       val url = args{0}
       process(url)
     }
     
    def processTopic(url : String, doc : Node) = {
             
       val comments = (doc \\ "div").filter( _ \ "@class" == "comm-container")
       val topic = new Topic(url, "delfi", url) 
       
       comments foreach {(com) =>
         topic.comments  ::= cmt (com)
       }  
       
       topic
    } 
     
     
    def cmt (com : scala.xml.Node) = {
            
      new Comment( 
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
