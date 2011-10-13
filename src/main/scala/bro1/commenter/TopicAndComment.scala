package bro1.commenter

import java.util.Date


class Comment (
  var id : Long,
  val remoteCommentID : String, 
  val timeStamp : Date, 
  val postedBy : String, 
  val text : String) {
  
  override def toString = {
    "id" + id + "\n" +
    "Comment ID: " + remoteCommentID + "\n" +
    "Posted at: " +  timeStamp + "\n" +
    "Author:" + postedBy + "\n" +
    text
  }

  /** 
   * Equality is checked based on ID and then if ID is -1 we check 
   * the comment's time and author. 
   */
  override def equals(other : Any) = {

    other match {
      case comment : Comment => {
        if (remoteCommentID != -1) {
          remoteCommentID == comment.remoteCommentID
        } else {
          timeStamp == comment.timeStamp && postedBy == comment.postedBy
        }
      }  
      case _ => false
    } 
    
  }
    
}


class EditableComment () {
  var postedBy : String = null
  var text : String = null
 
}


class Topic (
    var id : Long, 
    val title: String, 
    val topicType : String, 
    val url : String, 
    var lastChecked : Date = new Date(0), 
    var comments : List[Comment] = Nil) {

  /**
   * 
   * TODO: rework retrieval of produced to be URL based as described in the TopicProducerFactory.getInstance 
   */
  val producer : TopicProducer = topicType match {
      case "bernardinai" => BernardinaiTopicProducer
      case "delfi" => DelfiTopicProducer      
  }
  
  def getNextUpdate() : Option[Date] = {
    
    
    print(id + ":")
    print(lastChecked)
    print(":")
    print(getLatestCommentDate())
    print(":")
    val d = getNextUpdateD()
    print(d)
    print(":")
    println(comments.size)
    
    
    d
        
    /*
    val now = lastChecked.getTime() 
    val a = Some(new Date(now + 30 * 1000))
    println(a)
    a 
    */   
  }
  
  /**
   * The next update
   */
  def getNextUpdateD() : Option[Date] = {
    
    val latestCommentDate : Date = getLatestCommentDate()    
    val frequency = getFrequencyInSeconds()

    if (latestCommentDate.equals(new Date(0))) {
       if (lastChecked.equals(new Date(0))) {
         Some(new Date())
       } else {
         Some(new Date(lastChecked.getTime() + 20l * 60 * 1000))  
       }
    } else {    
    	val twoDaysLater = new Date(latestCommentDate.getTime() + 2l * 24 * 60 * 60 * 1000)
	    if (lastChecked after twoDaysLater) {
	      None
	    } else {
	      val next = new Date(latestCommentDate.getTime() + frequency * 1000l)
	      if (next.after(lastChecked)) {
	        Some(next)
	      } else {	
	        val s = (lastChecked.getTime() - latestCommentDate.getTime()) / 1000
	        val f = (frequency + s) / 2
	        Some(new Date(lastChecked.getTime() + f * 1000L))	        	        
	      }
	    }
    }
        
  }
  
  def getLatestCommentDate() = {
    comments match {
    	case Nil => new Date(0)
    	case _ => {
    		val latestComment = comments.maxBy(comment => comment.timeStamp)
    		latestComment.timeStamp
    	}
    }
  }
  
  /**
   * Get posting frequency of the last 10 comments.
   */
  def getFrequencyInSeconds() = {
          
    getFrequencyList() match {
      case Nil => 0
      case list => {list.sum / list.size}
    }
  }
  
  def getLast10Comments() = {
    val sortedComments = comments.sortBy(comment => comment.timeStamp).reverse
    sortedComments.take(10)
  }
    
  def insertToDb() = {
    Data.insertTopic(this)
  }


  def updateLastChecked() {
    lastChecked = new Date
    Data.updateTopicLastChecked(this)
  }
  
  def update() = {
	 updateLastChecked()	 
     val newComments = producer.processComments(this)
  }
  
  private def getFrequencyList(): List[Long] = {
    
    val lastComments = getLast10Comments();    
    var frequencies : List[Long] = Nil; 
    
    if(!lastComments.isEmpty) {
    
      lastComments.reduceLeft{
        (a: Comment, b: Comment)=> {      
          frequencies ::= (a.timeStamp.getTime - b.timeStamp.getTime) / 1000 
          b;
        }
      }
    }
    frequencies
  }
  
}

class BernardinaiTopic(id:Long, title: String, url : String) 
  extends Topic (id, title, "bernardinai", url)   


class DelfiTopic (id : Long, title: String, url : String) 
  extends Topic (id, title, "delfi", url)
