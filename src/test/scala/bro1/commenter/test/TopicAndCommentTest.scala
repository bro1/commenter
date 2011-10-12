package bro1.commenter.test

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone, Date}
import bro1.commenter._
import scala.swing.TextField
import java.net.URL
import bro1.commenter.test.TestUtil.getTimeLT


class TopicTimerTest {
    
    @Test
    def testFrequenciesInSecond {

      val topic = new Topic(1, "Test Topic Name", "delfi", "http://www.test.com")
      assertEquals(0, topic.getFrequencyInSeconds())
      
      topic.comments ::= new Comment(1, "b1", getTimeLT(2011, 9, 10, 10, 0, 0).getTime(), "testuser", "comment 1")
      assertEquals(0, topic.getFrequencyInSeconds())

      topic.comments ::= new Comment(2, "b2", getTimeLT(2011, 9, 10, 10, 0, 5).getTime(), "testuser", "comment 2")
      assertEquals(5, topic.getFrequencyInSeconds())
      
      topic.comments ::= new Comment(3, "b3", getTimeLT(2011, 9, 10, 10, 0, 9).getTime(), "testuser", "comment 3")
      assertEquals(4, topic.getFrequencyInSeconds())
      
      topic.comments ::= new Comment(4, "b4", getTimeLT(2011, 9, 10, 10, 0, 20).getTime(), "testuser", "comment 4")      
      assertEquals(6, topic.getFrequencyInSeconds()) // 5 + 4 + 11 = 20 / 3 = (int) 6
    }
    

    
    
    @Test
    def testLatestComment() {
      val topic = new Topic(1, "Test Topic Name", "delfi", "http://www.test.com")      
      assertEquals(new Date(0), topic.getLatestCommentDate())
      
      topic.comments = List(
    		  new Comment(1, "b1", getTimeLT(2011, 9, 10, 10, 0, 0).getTime(), "testuser", "comment 1"), 
    		  new Comment(2, "b2", getTimeLT(2011, 9, 10, 10, 0, 5).getTime(), "testuser", "comment 2"),
    		  new Comment(4, "b4", getTimeLT(2011, 9, 10, 10, 0, 20).getTime(), "testuser", "comment 4"),
    		  new Comment(3, "b3", getTimeLT(2011, 9, 10, 10, 0, 9).getTime(), "testuser", "comment 3"))
    		        
      assertEquals(getTimeLT(2011, 9, 10, 10, 0, 20).getTime(), topic.getLatestCommentDate())    		  
    }
    
    @Test
    def testNextUpdate() {
      
      val topic = new Topic(1, "Test Topic Name", "delfi", "http://www.test.com")      
      topic.comments = List(
    		  new Comment(1, "b1", getTimeLT(2011, 9, 10, 10, 0, 0).getTime(), "testuser", "comment 1"), 
    		  new Comment(2, "b2", getTimeLT(2011, 9, 10, 10, 0, 5).getTime(), "testuser", "comment 2"),
    		  new Comment(3, "b3", getTimeLT(2011, 9, 10, 10, 0, 9).getTime(), "testuser", "comment 3"),
    		  new Comment(4, "b4", getTimeLT(2011, 9, 10, 10, 0, 20).getTime(), "testuser", "comment 4"))
    		  
      println(topic.getLatestCommentDate())      
      println(topic.getFrequencyInSeconds())
      
      assertEquals(getTimeLT(2011, 9, 10, 10, 0, 26).getTime(), topic.getNextUpdate().get) // 5 + 4 + 11 = 20 / 3 = (int) 6
      
      topic.lastChecked = getTimeLT(2011, 9, 10, 10, 0, 40).getTime()
      assertEquals(getTimeLT(2011, 9, 10, 10, 0, 53).getTime(), topic.getNextUpdate().get)
      
      topic.lastChecked = getTimeLT(2011, 9, 10, 10, 0, 53).getTime()
      assertEquals(getTimeLT(2011, 9, 10, 10, 1, 12).getTime(), topic.getNextUpdate().get)
            
      topic.lastChecked = getTimeLT(2011, 9, 12, 10, 0, 53).getTime()
      assertEquals(None, topic.getNextUpdate())
      
      
    }
    
    

    
       
}
 