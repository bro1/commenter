import java.text.{SimpleDateFormat}
import java.util.Date
import java.io.{StringReader}
import scala.xml.Node

object FetchTopic {

     def main(args: Array[String]) {
       val url = args{0}
 
       processTopic (url)
 
     }
     
    def processTopic(url : String) = {
       val content = lj.scala.utils.http.download(url)
       
       val contentStringReader = new StringReader (content) 
              
       val doc = new TagSoupFactoryAdapter load contentStringReader
       
//       val comments = (doc \\ "div").filter( _.attribute("class").mkString == "comm-container")
       
       val comments = (doc \\ "div").filter( _ \ "@class" == "comm-container")
         
       val topic = new Topic(url, url) 
       
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
      
//       val n = ((com \ "div").filter(
//         _.attribute("class").mkString == "comm-text") \ "div") filter( _.attribute("class") == None)

//       val n = ((com \ "div").filter(
//         _ \ "@class" == "comm-text") \ "div") filter( _.attribute("class") == None)

         val n = ((com \ "div").filter(
         _ \ "@class" == "comm-text") \ "div").filter( _ \ "@class" != Some)

  
       val s = processChildren(n.first)
       println (s)
       s
      
    }
    
    
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
    
    def cleanUp(a : String) = {
      var s = a.replaceAll("&quot", "\"")
      
      if (s.startsWith("\n")) {
        s = s.substring(1)  
      }
      
      s
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
     
       val dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm");       
       
       
       dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Vilnius"))
       
       dateFormat.parse(strDate)
     
   }
   
              
    def getFrom(com : scala.xml.Node) = {
       
         ((com \ "div").filter(_.attribute("class").mkString == "comm-name") \ "strong").text

   } 


  
}
