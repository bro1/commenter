
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


object TopicCache {
  var topics : List[Topic] = List()
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
  
  
  def getSubscribtions() = {
    val ps = db.prepareStatement("select id, name, topictype, url from topic")
    val res = ps.executeQuery
    
    var topicSubscriptions : List[Topic] = List()
    
    while(res.next) {
      
      val id = res.getLong(1)
      val name = res.getString(2)
      val topicType = res.getString(3)
      val url = res.getString(4)
      
      topicSubscriptions = new Topic(id, name, topicType, url) :: topicSubscriptions
      
    }
    
    topicSubscriptions
    
  }
  
  
  def getCommentsForTopic(id : Long) = {
    
    var comments : List[Comment] = Nil
    
    val ps = db.prepareStatement("""
            select c.* from comment c 
            join topic t on t.id = c.topicid
            where t.id = ?"""    )
    ps.setLong(1, id)
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
  
  
  def saveTopic(topic : Topic) = {
    val ps = db.prepareStatement("""update topic set lastchecked = ? where  id = ?""")
    
    val lastChecked = new java.sql.Date(topic.lastChecked.getTime)
    
    ps.setDate(1, lastChecked)
    ps.setLong(2, topic.id)
    
    ps.execute
  }
  
  def insertTopic(topic : Topic) = {
    val ps = db.prepareStatement("""insert into topic (name, topictype, url) values (?, ?, ?)""")
    ps.setString(1, topic.title)
    ps.setString(2, topic.topicType)
    ps.setString(3, topic.url)
    
    ps.execute
    
    topic.id = lastID
    
    TopicCache.topics ::= topic
  } 
  
  
  val lastIDStatement = db.prepareStatement("""select last_insert_rowid()""")
  
  def lastID : Long = {
    val rs = lastIDStatement.executeQuery
    rs.next
    val id = rs.getLong(1)
    if (id == 0) throw new Exception("cannot read an ID - got 0")
    return id
  }
    
}

