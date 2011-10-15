package bro1.commenter

import java.net.URI
import java.sql.DriverManager
import scala.actors.Actor
import java.io.File
import java.util.Date


object TopicCacheUpdateActor extends Actor {
  def act() {    
    	react {
    	  case t : Topic => {
    	    TopicCache.topics ::= t
    	    //MainApplication.CommentsPanel.revalidate()
    	    TopicModel.fireTableDataChanged
    	    act()
    	    }
    	  case ("remove", top) => {
    	    TopicCache.topics = TopicCache.topics.remove(t => (t == top))
    	    TopicModel.fireTableDataChanged
    	    act()
    	  }
    	  case _ => {
    	    println("Unknown message")
    	    act()
    	  } 
    	}    
  }
}


object TopicCache {  
  var topics: List[Topic] = Nil
  var initialized = false  
}

object FileLocationHelper {
    val systemProperties = new scala.sys.SystemProperties
    
    val prefix = {      
      val sysPrefix = systemProperties.get("commenter.prefix")            
      if (sysPrefix.isDefined) {
        println(sysPrefix.get)
        sysPrefix.get
      } else {
        "."
      }
    }

    def get(fileName : String, mustExist : Boolean = true) = {
	    val fileLocation = prefix + File.separator + fileName
	    
	    println(fileLocation)
	    
	    val file = new java.io.File(fileLocation)
	    
	    if (mustExist && !file.exists()) {	      
	      val error = "File does not exist: " + file.getCanonicalPath()
	      println(error)
	      throw new Exception(error)
	    }
	    
	    file
    }   

}


object Data {

  val db = {

    val systemProperties = new scala.sys.SystemProperties
    
    val prefix = {      
      val sysPrefix = systemProperties.get("commenter.prefix")            
      if (sysPrefix.isDefined) {
        sysPrefix.get
      } else {
        "../../.."
      }
    }

    val dbLocation = prefix + "/misc/test.db"
    val f = new java.io.File(dbLocation)

    if (!f.exists) {
      println("Test DB file does not exist. Expected location: " + f.getCanonicalPath)
      throw new Exception("Main database not found")
    }

    val nativeDriverClass = Class.forName("org.sqlite.JDBC")
    DriverManager.getConnection("jdbc:sqlite:" + f.getCanonicalPath())
  }

  /**
   * Subscribes to a topic and returns the topic ID
   */
  def subscribe(name: String, topicType: String, url: String) = {

    val ps = db.prepareStatement("insert into topic(name, topictype, url, lastChecked) values (?, ?, ?, 0)");
    var i: Int = 1
    ps.setString(i, name); i += 1
    ps.setString(i, topicType); i += 1
    ps.setString(i, url); i += 1   

    val r = ps.executeUpdate
    
    
    
    val newID = lastID    
    TopicCacheUpdateActor ! (new Topic(newID, name, topicType, url, new Date(0)))
    newID
  }
  
  
  /**
   * Unsubscribes from a topic
   */
  def unsubscribe(topic: Topic) {
    deleteComments(topic)
    deleteTopic(topic)
    
    
        // TODO: delete from cache
    // TODO: select another topic

    TopicCacheUpdateActor ! ("remove", topic)
  }
  

  def getSubscribtions() = {
    
    if (!TopicCache.initialized) {
	    val ps = db.prepareStatement("select id, name, topictype, url, lastChecked from topic")
	    val res = ps.executeQuery
	
	    var topicSubscriptions: List[Topic] = Nil
	
	    while (res.next) {
	
	      val id = res.getLong(1)
	      val name = res.getString(2)
	      val topicType = res.getString(3)
	      val url = res.getString(4)
	      val date = new java.util.Date(res.getDate("lastChecked").getTime())
	      
	      val commentsFromDB = getCommentsForTopic(id)
	      
	      println(commentsFromDB.size)
	
	      topicSubscriptions ::= new Topic(id, name, topicType, url, lastChecked = date, comments = commentsFromDB) //:: topicSubscriptions	
	    }
	    	
	    TopicCache.topics = topicSubscriptions
	    TopicCache.initialized = true
    }

    TopicCache.topics
  }

  def getCommentsForTopic(topicID: Long) = {

    var comments: List[Comment] = Nil

    val ps = db.prepareStatement("""
            select c.* from remotecomment c 
            join topic t on (t.id = c.topicid)
            where t.id = ?""")
            
    ps.setLong(1, topicID)
    val rs = ps.executeQuery
    
    while (rs.next) {
      val time = rs.getDate("time")
      val commentText = rs.getString("comment")
      val id = rs.getInt("id")
      val remoteCommentID = rs.getString("remotecommentid")
      val postedBy = rs.getString("author")

      comments ::= new Comment(id, remoteCommentID, time, postedBy, commentText)
    }

    comments

  }

  def updateTopicLastChecked(topic: Topic) = {
    val ps = db.prepareStatement("""update topic set lastchecked = ? where  id = ?""")

    val lastChecked = new java.sql.Date(topic.lastChecked.getTime)

    ps.setDate(1, lastChecked)
    ps.setLong(2, topic.id)

    ps.execute
  }
  
  
  def saveComments(topic : Topic, comments : List[Comment]) {
    val statement = db.prepareStatement("""insert into remotecomment (topicID, remoteCommentID, author, comment, time) values (?, ?, ?, ?, ?)""")
    
	  for (comment <- comments) {
	      
		  var i = 1;
		  statement.setLong(i, topic.id); i += 1
		  statement.setString(i, comment.remoteCommentID); i += 1 
		  statement.setString(i, comment.postedBy); i += 1
		  statement.setString(i, comment.text); i += 1
		  statement.setDate(i, new java.sql.Date(comment.timeStamp.getTime())); i += 1
		  
		  statement.executeUpdate()
		  comment.id = lastID
	  }
  }

  def insertTopic(topic: Topic) = {
    val ps = db.prepareStatement("""insert into topic (name, topictype, url) values (?, ?, ?)""")
    ps.setString(1, topic.title)
    ps.setString(2, topic.topicType)
    ps.setString(3, topic.url)

    ps.execute

    topic.id = lastID

    TopicCache.topics ::= topic
  }

  val lastIDStatement = db.prepareStatement("""select last_insert_rowid()""")

  def lastID: Long = {
    val rs = lastIDStatement.executeQuery
    rs.next
    val id = rs.getLong(1)
    if (id == 0) throw new Exception("cannot read an ID - got 0")
    return id
  }
  
  private def deleteComments(topic: Topic) {

    val ps = db.prepareStatement("delete from remotecomment where topicid = ?");    
    ps.setLong(1, topic.id);     
    ps.executeUpdate
  }
  
  private def deleteTopic(topic: Topic) {
    val ps = db.prepareStatement("delete from topic where id = ?")    
    ps.setLong(1, topic.id)
    ps.executeUpdate
  }
  
  
}