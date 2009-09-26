import scala.dbc._
import scala.dbc.syntax._
import scala.dbc.syntax.Statement._
import scala.dbc.statement._
import scala.dbc.value._
import java.net.URI


import swing._
import event._


object SubscribeToTopicWindow extends Frame {
  title = "Subscribe to Article"
  
  object buttonCancel extends Button("Cancel")
  
  object buttonSubscribe extends Button("Subscribe")

  object buttonPannel extends FlowPanel {    
      
      contents += buttonCancel
      contents +=  buttonSubscribe 
    
  }
  
  
  contents = new GridBagPanel {
    
    object urlField extends TextField {columns = 20}  
    
    layout(new Label("Vardas: ")) = new Constraints {gridx = 0; gridy = 0}
    layout(urlField) = new Constraints {gridx = 1; gridy = 0; }
    layout(new Label("Topic Name: ")) = new Constraints {gridx = 0; gridy = 2; gridwidth = 2; fill = GridBagPanel.Fill.Both }
    layout(new Label("Topic Type: ")) = new Constraints {gridx = 0; gridy = 3; gridwidth = 2; fill = GridBagPanel.Fill.Both }
    
    layout(buttonPannel) = new Constraints {gridx = 0; gridy = 4; gridwidth = 2; anchor = GridBagPanel.Anchor.CENTER }
    
    
  }
  
  
  listenTo(buttonCancel, buttonSubscribe)
  
  reactions += {
    case ButtonClicked(`buttonCancel`) => {
      SubscribeToTopicWindow.visible = false;
    }
  }
   
} 


object SubscribeToTopic {
  
  def main(args: Array[String]) {
    dbctest
  }

  
  
}

object SqliteVendor extends Vendor {
  val uri = new URI("jdbc:sqlite:misc/test.db")
  val user = ""
  val pass = ""
  
  val retainedConnections = 1
  val nativeDriverClass = Class.forName("org.sqlite.JDBC")
  val urlProtocolString = "jdbc:sqlite:"
  
}

object typ {
  def char(v : String) = {Utilities.valueToConstant( new CharacterVarying {
    val dataType = DataTypeUtil.characterVarying(255)
    val nativeValue = v
  })}
  
  def int (v : Int) = {
    Utilities.valueToConstant( new ExactNumeric[int] {
    val dataType = DataTypeUtil.integer
    val nativeValue = v
  })
  }
  
}

object dbctest  {
  
  def subscribe(name : String, topicType : String, url : String) = { 
  
    val db = new Database(SqliteVendor)
     
    val data = InsertionData.Constructor(
      Some(List("name", "topictype", "url")), 
      List(typ char name , typ char topicType , typ char url ))
   
    db executeStatement  Insert ("topic", data)
  
  }
}
