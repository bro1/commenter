package bro1.commenter.test

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone, Date}
import bro1.commenter._
import scala.swing.TextField
import java.net.URL


class TopicTimerTest {
    
    @Test
    def testFrequenciesInSecond {

      val t = new Topic(1, "Test Topic Name", "delfi", "http://www.test.com")
      assertEquals(0, t.getFrequencyInSeconds())
      
      t.comments ::= new Comment(1, "b1", TestUtil.getTimeLT(2011, 9, 10, 10, 0, 0).getTime(), "testuser", "comment 1")
      assertEquals(0, t.getFrequencyInSeconds())

      t.comments ::= new Comment(2, "b2", TestUtil.getTimeLT(2011, 9, 10, 10, 0, 5).getTime(), "testuser", "comment 2")
      assertEquals(5, t.getFrequencyInSeconds())
      
      t.comments ::= new Comment(3, "b3", TestUtil.getTimeLT(2011, 9, 10, 10, 0, 9).getTime(), "testuser", "comment 3")
      assertEquals(4, t.getFrequencyInSeconds())
      
      
      t.comments ::= new Comment(4, "b4", TestUtil.getTimeLT(2011, 9, 10, 10, 0, 20).getTime(), "testuser", "comment 4")      
      assertEquals(6, t.getFrequencyInSeconds()) // 5 + 4 + 11 = 20 / 3 = (int) 6
    }
    

    
       
}
 