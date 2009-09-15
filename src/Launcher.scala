import java.io.{File,FileReader}
import java.text.{SimpleDateFormat}
import java.util.Date

import java.util.TimeZone
  
object Launcher {
    
   def main(args: Array[String]) {
      
     val url = new FileReader (new java.io.File ("misc/examples/delfi/str1p1.html"))
     val zz = new TagSoupFactoryAdapter load url

     val comments = (zz \\ "div").filter( _.attribute("class").mkString == "comm-container")
         
     comments foreach {(com) =>
        cmt (com)
     }
      
    
      
   }
   
   
   def cmt (com : scala.xml.Node) {
       println ("Nuo: " + 
         ((com \ "div").filter(_.attribute("class").mkString == "comm-name") \ "strong").text)
     
       dt(com)
       
       val c =  (com \ "div").filter(
         _.attribute("class").mkString == "comm-text")       
       
       val alerts = (c.first \ "div").filter(_.attribute("class").mkString != "comm-alerts")
       
       println (alerts)            
     
   }
   
   
   def dt(com : scala.xml.Node)  {
     
     val strDate =            
         ((com \ "div").filter(_.attribute("class").mkString == "comm-name") \ "div").text
     
       val df = new SimpleDateFormat("yyyy MM dd HH:mm");       
       
       
       df.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Vilnius"))
       val d = df.parse(strDate)

       
       println(d)       
       //2009 09 13 10:48
     
   }
   
   
  
  
}
