
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
      var topicSubscriptions : List[Topic] = Nil

      
      topicSubscriptions = new Topic(
          "Sample topic",
          "delfi",
          "file://misc/examples/delfi/str1p1.html"
      ) :: topicSubscriptions

      
      topicSubscriptions = new Topic(
          "Sample topic bernardinai",
          "bernardinai",
          "file://misc/examples/bernardinai/bernardinai2.html"
      ) :: topicSubscriptions
      
      topicSubscriptions
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
  
  
  def getCommentsForTopic(url : String) {
    
    var comments : List[Comment] = Nil
    
    val ps = db.prepareStatement("""
            select c.* from comment c 
            join topic t on t.id = c.topicid
            where t.url = ?"""    )
    ps.setString(1, url)
    val rs = ps.executeQuery
    while(rs.next) {
      val time = rs.getDate("time")
      val commentText = rs.getString("comment")
      val id = rs.getInt("id")
      val remoteCommentID = rs.getString("remotecommentid")
      val postedBy = rs.getString("author")
      
      comments ::= new Comment(id, remoteCommentID, time, postedBy, commentText)
    }
    
    comments
    
  }
    
}

