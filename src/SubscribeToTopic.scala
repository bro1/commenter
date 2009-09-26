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
  
  object urlField extends TextField {columns = 20}  
  
  contents = new GridBagPanel {
    
  
    
    layout(new Label("Vardas: ")) = new Constraints {gridx = 0; gridy = 0}
    layout(urlField) = new Constraints {gridx = 1; gridy = 0; }
    layout(new Label("Topic Name: ")) = new Constraints {gridx = 0; gridy = 2; gridwidth = 2; fill = GridBagPanel.Fill.Both }
    layout(new Label("Topic Type: ")) = new Constraints {gridx = 0; gridy = 3; gridwidth = 2; fill = GridBagPanel.Fill.Both }
    
    layout(buttonPannel) = new Constraints {gridx = 0; gridy = 4; gridwidth = 2; anchor = GridBagPanel.Anchor.CENTER }    
    
  }
  
  
  listenTo(buttonCancel, buttonSubscribe)
  
  reactions += {
    case ButtonClicked(`buttonCancel`) => {
      SubscribeToTopicWindow.visible = false      
    }
    
   case ButtonClicked(`buttonSubscribe`) => {
      subscribe(urlField.text)
      SubscribeToTopicWindow.visible = false      
    }
    
    case EditDone(`urlField`) => {
      println("Edit done") 
    }
    
  }
  
  
  def subscribe(url : String) {
    dbctest.subscribe(url, "", url)
  }
   
} 




object SubscribeToTopic {
  
  def main(args: Array[String]) {
    dbctest getSubscribtions
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
  
  val db = new Database(SqliteVendor)
  
  def subscribe(name : String, topicType : String, url : String) = { 
  
    val data = InsertionData.Constructor(
      Some(List("name", "topictype", "url")), 
      List(typ char name , typ char topicType , typ char url ))
   
    db executeStatement  Insert ("topic", data)  
  }
  
  object t extends scala.dbc.statement.Table {
    def tableName = "topic" 
    
    def tableRename = None
    
    def fieldTypes = List(DataTypeUtil.integer, DataTypeUtil.characterVarying(255), DataTypeUtil.characterVarying(255), DataTypeUtil.characterVarying(255))
  }
  
  
  object sel extends scala.dbc.statement.Select {
    def havingClause = None
    def groupByClause = None
    def whereClause = None
    def fromClause = List(t)
    
    
    def selectList = List()
    
    def setQuantifier = None
    
    def fieldTypes = List(DataTypeUtil.integer, DataTypeUtil.characterVarying(255), DataTypeUtil.characterVarying(255), DataTypeUtil.characterVarying(255))
  }
  
  
  def getSubscribtions() = {    
    
    db executeStatement (sel)
  }
  
  
  
}
