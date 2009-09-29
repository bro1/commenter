import java.io.{File,FileReader}
import java.text.{SimpleDateFormat}
import java.util.Date
import scala.xml._


import java.util.TimeZone
  
object Launcher {
    
    def main(args: Array[String]) {
        // delfiLinks
        bernardinaiComments 
    }


    def bernardinaiComments = {
        val url = new FileReader (new File("misc/examples/bernardinai/bernardinai1.html"))
        val doc = new TagSoupFactoryAdapter load url

        //val coms = doc \\ "div".filter(!_.attribute("class").isEmpty && _.attribute("class").get.text == "comment")
        //val hasClass = new HasKeyValue("class")
        val coms = (doc \\ "div").filter(_.attribute("class").mkString == "comment")

        coms foreach {(com) =>
            cmt (com)
        }  

        //println(coms)
    }

    def cmt(node : Node) = {
        
        val coms = (node \\ "div").filter(_.attribute("class").mkString == "text")
        val s = FetchTopic.processChildren(coms.first)
        println("-----------------------")
        println(s)
        val commentDate = getDate(node)
        println(commentDate)
        println(getFrom(node))
    }

    def getFrom (node : Node)  = {
        val coms = (node \\ "span").filter(_.attribute("class").mkString == "author")
        FetchTopic.cleanUp(coms.first.text)
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
