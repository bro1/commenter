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
  
}

class BernardinaiTopic(title: String, url : String) 
  extends Topic (title, "bernardinai", url) {
    
}
  

class DelfiTopic (title: String, url : String) 
  extends Topic (title, "delfi", url) {    
    
  
}