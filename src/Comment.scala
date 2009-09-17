import java.util.Date

abstract class Comment (
  val id : String, val postedAt : Date, val postedBy  : String , val text : String) {
  
  override def toString = {
    "Comment ID: " + id + "\n" +
    "Posted at: " +  postedAt + "\n" +
    "Author:" + postedBy + "\n" +
    text    
  }
    
}
