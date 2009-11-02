import java.text.{SimpleDateFormat}
import java.util.Date
import java.io.{StringReader,File,FileReader}
import scala.xml.Node


package lj.scala.utils {


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

}