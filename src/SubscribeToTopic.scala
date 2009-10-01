//import scala.dbc._
//import scala.dbc.syntax._
//import scala.dbc.syntax.Statement._
//import scala.dbc.statement._
//import scala.dbc.value._
import java.net.URI
import java.sql.DriverManager


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
    Data.subscribe(url, "", url)
  }
   
} 




object SubscribeToTopic {
  
  def main(args: Array[String]) {
    Data getSubscribtions
  }

  
  
}

/*
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

*/

object Data {
  val db = {
    val nativeDriverClass = Class.forName("org.sqlite.JDBC")
    DriverManager.getConnection("jdbc:sqlite:misc/test.db")    
  }
  
  def subscribe(name : String, topicType : String, url : String) = { 
              
      val ps = db.prepareStatement("insert into topic(name, topictype, url) values (?, ?, ?)");
      var i : Int = 1      
      ps.setString(i, name) ; i += 1
      ps.setString(i, topicType) ; i += 1
      ps.setString(i, url) ; i += 1
                             
      val r = ps.executeUpdate
      var idr = ps.getGeneratedKeys
        
      if (idr.next) {
        Some(idr.getInt(1))
      } else {
        None
      }                              
                        
      
  }
  

  def getSampleSubscriptions() = {
      val topicSubscriptions : List[Topic] = List()

      
      new Topic(
          "Sample topic",
          "delfi",
          "file://misc/examples/delfi/str1p1.html"
      ) :: topicSubscriptions

      
      new Topic(
          "Sample topic bernardinai",
          "bernardinai",
          "file://misc/examples/bernardinai/bernardinai2.html"
      ) :: topicSubscriptions
  }
  
  def getSubscribtions() = {
    val ps = db.prepareStatement("select name, topictype, url from topic")
    val res = ps.executeQuery
    
    val topicSubscriptions : List[Topic] = List()
    
    while(res.next) {
      
      val name = res.getString(1)
      val topicType = res.getString(2)
      val url = res.getString(3)
      
      new Topic(name, topicType, url) :: topicSubscriptions
      
    }
    
    topicSubscriptions
    
  }
 
    
    
//      public Connection connect() throws ClassNotFoundException, SQLException {
//
//    Class.forName(driverClassName);
//
//    String jdbcUrl = getJdbcUrl();
//    Connection db = DriverManager.getConnection(jdbcUrl, username, password); // connect to the db
//    
//    return db;
//  }

    
}

/*
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
*/
