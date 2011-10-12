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
      Thread.sleep(1000)
    }
  }

  def schedule {

    val currentTime = new java.util.Date()

    def dateFilter(topic: Topic) = {
      val nextUpdate = topic.getNextUpdate()
      nextUpdate.isDefined && (currentTime after nextUpdate.get)
    }

    val topicsDue = Data.getSubscribtions().filter(dateFilter)
    for (topic <- topicsDue) {
      topic.lastChecked = currentTime
      ArticleCheckActor ! topic
    }

  }

}

/**
 * TODO: implement ArticleCheckActor 
 */
object ArticleCheckActor extends Actor {

  def act() {
    while (true) {
      receive {
        case t: Topic => {
          updateTopic(t)
        }
      }
      Thread.sleep(1000)
    }
  }
  
  private def updateTopic(t: Topic): Unit = {
    val newComments = t.update();
    if (!newComments.isEmpty) {
      
    }
  }
}