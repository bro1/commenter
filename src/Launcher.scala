import java.io.{File,FileReader}
<<<<<<< HEAD:src/Launcher.scala
import java.text.{SimpleDateFormat}
import java.util.Date
import scala.xml.{Node}

=======
>>>>>>> 025e28376c0b8047d070fc542d141482ecb8b7f4:src/Launcher.scala

import java.util.TimeZone
  
object Launcher {
    
   def main(args: Array[String]) {
     
     
      
     val url = new FileReader (new java.io.File ("misc/examples/delfi/str1p1.html"))
     val zz = new TagSoupFactoryAdapter load url
      
     download (urls(zz)) 
     
     
     println (zz)
      
   }
   
   def download (strings : Seq[Node] ) {
     
     //map(strings => _.attribute("href").get.text)
//     strings foreach {(s) => {
//         
//           println (s.attribute("href").get.text)        
//       
//       } 
//     }
   
   println (strings.map(s=> s.attribute("href")))
   
   }
   
   def urls(doc : Node)  = 
      (doc \\ "a").filter(_.attribute("class").mkString == "comNav") 
   
   
   
   
   
  
  
}
