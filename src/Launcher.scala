import java.io.{File,FileReader}
import java.text.{SimpleDateFormat}
import java.util.Date
import scala.xml._


import java.util.TimeZone
  
object Launcher extends TopicProducer {
    
    def main(args: Array[String]) {
        // delfiLinks
        bernardinaiComments 
    }


    def bernardinaiComments = {
      
      process("file://" + "misc/examples/bernardinai/bernardinai2.html");
      
    }
    
    def processTopic(url : String, doc: Node) = {
      val topic = new Topic(url, "bernardinai", url) 
      val coms = (doc \\ "div").filter(_.attribute("class").mkString == "comment")
      coms foreach {(com) =>
        topic.comments  ::= cmt (com)        
      }  
      topic 
   }
    

    def cmt(node : Node) = {
      new Comment( 
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
