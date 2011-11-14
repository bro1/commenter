package bro1.commenter

import java.util.Date
import bro1.commenter.impl.delfi.DelfiTopicProducer

import java.net.URL


class Topic (
    var id : Long, 
    val title: String, 
    val topicType : String, 
    val url : String, 
    var lastChecked : Date = new Date(0), 
    var comments : List[Comment] = Nil) {
  
  var newComments = false
  
  var uiCache : MainApplication.CommentsPanel = null
  
  def getURL =  new URL(url)
  
  def commentsSorted = {
    comments.sortBy(_.timeStamp)    
  }

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
    val d = getNextUpdateD()
    print(d)
    print(":")
    println(title)   
    
    d
        
  }
  
  /**
   * The next update
   */
  def getNextUpdateD() : Option[Date] = {
    
    val latestCommentDate : Date = getLatestCommentDate()    
    val frequency = getFrequencyInSeconds()

    if (latestCommentDate.equals(new Date(0))) {
       if (lastChecked.equals(new Date(0))) {
         Some(new Date(new Date().getTime() - 1000))
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
  
  
  def markAllCommentsRead() {
    if (newComments) {
      newComments = false
      TopicModel.fireTableDataChanged()
    } 
  }
  
  
  def unsubscribe() {
    Data.unsubscribe(this)        
  }  
  

  
}

// TODO: do we really need classes BernardinaiTopic and DelfiTopic?  
class BernardinaiTopic(id:Long, title: String, url : String) 
  extends Topic (id, title, "bernardinai", url)   


class DelfiTopic (id : Long, title: String, url : String) 
  extends Topic (id, title, "delfi", url)
