import java.io.{File,FileReader}

import java.util.TimeZone
  
object Launcher {
    
   def main(args: Array[String]) {
     
     
      
     val url = new FileReader (new java.io.File ("misc/examples/delfi/str1p1.html"))
     val zz = new TagSoupFactoryAdapter load url
      
     println (zz)
      
   }
   
   
   
   
  
  
}
