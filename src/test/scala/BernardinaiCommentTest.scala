import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}

import lj.scala.utils.TagSoupFactoryAdapter

object TestUtil {
  
  private val tz = java.util.TimeZone.getTimeZone("Europe/Vilnius")  
  
  def getTimeLT(year : Int, month : Int, day : Int, hour : Int, minute : Int, second : Int) = {
    
        val postedAt = new java.util.GregorianCalendar(tz)
        postedAt.set(year, month, day, hour, minute, second)
        postedAt.set(Calendar.MILLISECOND, 0)
        
        postedAt
  	}
}

class BernardinaiCommentTest {

    @Before
    def setUp: Unit = {
    }

    @After
    def tearDown: Unit = {
    }

    @Test
    def testExampleTest {

        val reader = BernardinaiTopicProducer.getReader("file://misc/examples/bernardinai/bernardinainew1.html")

        val doc = new TagSoupFactoryAdapter load reader

        val comments = BernardinaiTopicProducer.extractAllComments(doc)

        assertTrue("Comments size should be more than 0", comments.size > 0)

        assertEquals("Comments size should be 8", 8, comments.size)
        assertEquals("Geras Ateistas", (comments{0}.postedBy))
        
        val postedAt = TestUtil.getTimeLT(2009, 10, 6, 0, 5, 0)
        
        assertEquals(postedAt.getTime, (comments{0}.postedAt))
        assertEquals("100739", comments{0}.remoteCommentID)

        val text = comments{0}.text
      
        println(text)

        assertTrue(text.startsWith("Puiku, mokyklose"))

        // Also check the last comment

        val lastCommentPostedAt = TestUtil.getTimeLT(2009, 10, 4, 23, 14, 00)
        assertEquals("drager", (comments{7}.postedBy))
        assertEquals(lastCommentPostedAt.getTime, (comments{7}.postedAt))

    }

}


class DelfiCommentTest {
    
    @Test
    def testExampleTest {

        val reader = DelfiTopicProducer.getReader("file://misc/examples/delfi/str1p1.html")

        val doc = new TagSoupFactoryAdapter load reader

        val comments = DelfiTopicProducer.extractAllComments(doc)

        assertTrue("Comments size should be more than 0", comments.size > 0)

        assertEquals("Number of comments does not match", 20, comments.size)

        val firstComment = comments{0}

        assertEquals("to Arnas", (firstComment.postedBy))
        val firstCommentPostedAt = TestUtil.getTimeLT(2009, 8, 13, 11, 36, 00)
        assertEquals(firstCommentPostedAt.getTime, (firstComment.postedAt))
        assertEquals("24022150", firstComment.remoteCommentID)

        val text = firstComment.text        

        assertTrue(text.startsWith("godumas,pinigai,noras valdyti"))
        assertTrue(text.endsWith("ir Hitleriui niekas paminklų nestato..."))

        // Also check the last comment

        val lastCommentPostedAt = TestUtil.getTimeLT(2009, 8, 12, 23, 31, 00)

        val lastComment = comments{19}

        assertEquals("to :)", (lastComment.postedBy))
        assertEquals(lastCommentPostedAt.getTime, (lastComment.postedAt))

    }
       
}
