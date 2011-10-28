package bro1.commenter
import java.util.Date


class Comment (
  var id : Long = -1,
  val remoteCommentID : String = "", 
  val timeStamp : Date = null, 
  val postedBy : String = "", 
  val text : String = "",
  val email : String = null) {
  
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

