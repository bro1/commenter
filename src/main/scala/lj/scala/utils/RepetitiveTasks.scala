package lj.scala.utils

import scala.actors.Actor
import bro1.commenter._

object RepetitiveTasks {

  var repeatEvery: Int = 1000;

  def oncePerSecond(callback: () => Unit): Unit =
    {
      while (true) {
        callback()
        Thread.sleep(repeatEvery)
      }
    }

}

/**
 * This actor scans all the topics to see if any of them are due for refresh
 * and forwards the ones that are to another actor.
 */
object TimeActor extends Actor {
  
  def act() {
    while (true) {
      schedule
      Thread.sleep(10000)
    }
  }

  def schedule {

    def dateFilter(topic: Topic) = {
      val nextUpdate = topic.getNextUpdate()
      val currentTime = new java.util.Date()
      nextUpdate.isDefined && (currentTime after nextUpdate.get)
    }

    val topicsDue = Data.getSubscribtions().filter(dateFilter)
    for (topic <- topicsDue) {
      topic.updateLastChecked()
      WebAccessActor ! topic
    }

  }

}

/**
 * TODO: implement ArticleCheckActor 
 */
object WebAccessActor extends Actor {

  def act() {
    
    loop {
      // We do not really need to sleep - just process a message if it's available
      //Thread.sleep(1000)
      receive {
        case t: Topic => {
          updateTopic(t)
        } 
        case post : Post => {
          "Post message received"
          postCommentToTopic(post)
        } 
        case a => {print("Unknown:"); println(a)}
      }
    }
  }

  private def updateTopic(t: Topic): Unit = {
    val newComments = t.update();
    
    // TODO: update the data grid if it is not empty
//   if (!newComments.isEmpty) {
////      Data.saveComments(t, newComments)
//    }

  }
  
  
  
  private def postCommentToTopic(post : Post) {    
    val topicProducer = TopicProducerFactory.getInstance(post.topic.getURL)
    val poster = topicProducer.get.getPoster()    
    poster.post(post.topic, post.comment)
    updateTopic(post.topic)
  }
  
}


class Post(val topic:Topic, val comment:Comment) {}