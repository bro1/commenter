package bro1.commenter

import java.net.URI
import java.net.URL
import java.sql.DriverManager
import scala.actors.Actor

import swing._
import event._

object SubscribeToTopicWindow extends Frame {  
  title = "Subscribe to Article"
  
  TopicNameActor.start()

  object buttonCancel extends Button("Cancel")

  object buttonSubscribe extends Button("Subscribe")

  object buttonPannel extends FlowPanel {

    contents += buttonCancel
    contents += buttonSubscribe

  }

  object urlField extends TextField {columns = 60}
  
  object nameField extends TextField {columns = 60}

  contents = new GridBagPanel {

    layout(new Label("URL: ")) = new Constraints { gridx = 0; gridy = 0 }
    layout(urlField) = new Constraints { gridx = 1; gridy = 0; }
    layout(new Label("Topic Name: ")) = new Constraints { gridx = 0; gridy = 1;  fill = GridBagPanel.Fill.Both }
    layout(nameField) = new Constraints { gridx = 1; gridy = 1; }
    layout(new Label("Topic Type: ")) = new Constraints { gridx = 0; gridy = 2; gridwidth = 2; fill = GridBagPanel.Fill.Both }

    layout(buttonPannel) = new Constraints { gridx = 0; gridy = 4; gridwidth = 2; anchor = GridBagPanel.Anchor.Center }

  }

  listenTo(buttonCancel, buttonSubscribe, urlField)

  reactions += {
    case ButtonClicked(`buttonCancel`) => {
      SubscribeToTopicWindow.visible = false
    }

    case ButtonClicked(`buttonSubscribe`) => {      
      subscribe(urlField.text)            
      resetFields
      
      SubscribeToTopicWindow.visible = false
    }    
    
    case EditDone(`urlField`) => {      
      println("Edit done:" + urlField.text)
      TopicHelper.updateName(nameField, urlField)
    }
    

  }
  
  
  def resetFields() {
    urlField.text = ""
    nameField.text = ""
  }

  def subscribe(urlString: String) : Boolean = {    
    val url = new URL(urlString)
    val topicProducerOption = TopicProducerFactory.getInstance(url)    
    if (topicProducerOption.isDefined) {
    	val actualURL  = topicProducerOption.get.matchesPattern(url).get
    	Data.subscribe(nameField.text, topicProducerOption.get.categoryName,  actualURL.toString())
    	true
    } else false
  }

}


object TopicHelper {
  def updateName(nameField : TextField, urlField : TextField) {
    if (nameField.text.isEmpty() && isValidURL(urlField)) {
      TopicNameActor ! urlField.text
      
    }
  }
  
  /** 
   * Check if URL is non-empty parsable URL. 
   * */
  def isValidURL(url : TextField) = {
    url.text.isEmpty();
    
    val option = try {
      val articleURL = new java.net.URL(url.text) 
      Option(articleURL)
    } catch {
      case _ => None
    }
    
    option.isDefined    
  }
  
  
  def getName(urlString : String) = {
    val url = new java.net.URL(urlString)
    val t = TopicProducerFactory.getInstance(url)
    if (t.isDefined) {
      Option(t.get.getTitle(url))
    } else {
      None
    }
  }
  
}


object SubscribeToTopic {

  def main(args: Array[String]) {
    Data getSubscribtions
  }

}



/**
 * This actor gets the name from the URL and then updates the 
 * name TextField
 */
 object TopicNameActor extends Actor {
   
   def act() {
     while(true) {
       receive {
         case msg : String => {
           getNameAndUpdateTextField(msg)
         }
       }
     }
   } 
   
   private def getNameAndUpdateTextField(msg: String): Unit = {           
     val name = try { 
	    	   TopicHelper.getName(msg)
	    	 } catch {
	    	   case _ => None 
	    	 }
     
     if (name.isDefined) {	    	   
       SubscribeToTopicWindow.nameField.text = name.get
     }
   }
   
 } 

