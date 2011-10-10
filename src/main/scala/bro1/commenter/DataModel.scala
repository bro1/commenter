package bro1.commenter

import java.net.URI
import java.sql.DriverManager
import scala.actors.Actor


object TopicCache {
  var topics: List[Topic] = Nil
  var initialized = false  
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

  def subscribe(name: String, topicType: String, url: String) = {

    val ps = db.prepareStatement("insert into topic(name, topictype, url) values (?, ?, ?)");
    var i: Int = 1
    ps.setString(i, name); i += 1
    ps.setString(i, topicType); i += 1
    ps.setString(i, url); i += 1

    val r = ps.executeUpdate
    var idr = ps.getGeneratedKeys

    if (idr.next) {
      Option(idr.getInt(1))
    } else {
      None
    }

  }

  def getSubscribtions() = {
    
    if (!TopicCache.initialized) {
	    val ps = db.prepareStatement("select id, name, topictype, url from topic")
	    val res = ps.executeQuery
	
	    var topicSubscriptions: List[Topic] = List()
	
	    while (res.next) {
	
	      val id = res.getLong(1)
	      val name = res.getString(2)
	      val topicType = res.getString(3)
	      val url = res.getString(4)
	
	      topicSubscriptions = new Topic(id, name, topicType, url) :: topicSubscriptions
	
	    }
	
	    TopicCache.topics = topicSubscriptions
	    TopicCache.initialized = true
    }

    TopicCache.topics
  }

  def getCommentsForTopic(id: Long) = {

    var comments: List[Comment] = Nil

    val ps = db.prepareStatement("""
            select c.* from comment c 
            join topic t on t.id = c.topicid
            where t.id = ?""")
    ps.setLong(1, id)
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

  def saveTopic(topic: Topic) = {
    val ps = db.prepareStatement("""update topic set lastchecked = ? where  id = ?""")

    val lastChecked = new java.sql.Date(topic.lastChecked.getTime)

    ps.setDate(1, lastChecked)
    ps.setLong(2, topic.id)

    ps.execute
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
  
}