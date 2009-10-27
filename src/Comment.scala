import java.util.Date


class Comment (
  val id : Int,
  val remoteCommentID : String, val postedAt : Date, val postedBy  : String , val text : String) {
  
  override def toString = {
    "id" + id + "\n" +
    "Comment ID: " + remoteCommentID + "\n" +
    "Posted at: " +  postedAt + "\n" +
    "Author:" + postedBy + "\n" +
    text    
  }
    
}


class EditableComment () {
  var postedBy : String = null
  var text : String = null
 
}



class Topic (val title: String, val topicType : String, val url : String) {  
  
  
  var comments :  List[Comment] = List()
  
  def getTimeOfNextUpdate = {
    val latestCommentDate : java.util.Date = getLatestCommentDate();
    
    val frequency = getFrequencyInSeconds();
  }
  
  def getLatestCommentDate() = {
    new java.util.Date()
  }
  
  /**
   * Get posting frequency of the last 10 comments.
   */
  def getFrequencyInSeconds() = {
    val lastComments = getLast10Comments();
    
    var frequencies : List[Long] = List(); 
    
    if(!lastComments.isEmpty) {
    
      lastComments.reduceLeft{
        (a: Comment, b: Comment)=> {      
          frequencies = (a.postedAt.getTime - b.postedAt.getTime) :: frequencies 
          b;
        }
      }
    }
    
    println(frequencies)
    
    val size = frequencies.size    
    
    if (size != 0) {
      val sum = (0L :: frequencies).reduceLeft((a:Long, b:Long) => {
        a + b
      })
      
      sum / size
    } else {
      0
    }
    
    
    
  }
  
  def getLast10Comments() = {
    comments
  }  
  
}

class BernardinaiTopic(title: String, url : String) 
  extends Topic (title, "bernardinai", url) 
    


class DelfiTopic (title: String, url : String) 
  extends Topic (title, "delfi", url)     
