package bro1.commenter

import java.util.Date


class Comment (
  val id : Int,
  val remoteCommentID : String, val timeStamp : Date, val postedBy  : String , val text : String) {
  
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
        if (id != -1) {
          id == comment.id
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


class Topic (var id : Long, val title: String, val topicType : String, val url : String) {

  val producer : TopicProducer = topicType match {
      case "bernardinai" => BernardinaiTopicProducer
      case "delfi" => DelfiTopicProducer
  }
  
  var lastChecked : Date = new Date(0);
  
  var comments :  List[Comment] = Nil
  
  /**
   * The next update
   */
  def getNextUpdate() : Option[Date] = {
    
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
  
  def setLastCheckedNow() = {
    lastChecked = new Date();
    saveToDb()
  }
  
  def saveToDb () = {
    Data.saveTopic(this)    
  }
  
  def insertToDb() = {
    Data.insertTopic(this)
  }


  def updateLastChecked() {
    lastChecked = new Date
  }
  
  def update() = {
	 updateLastChecked()
     producer.processComments(this)
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
