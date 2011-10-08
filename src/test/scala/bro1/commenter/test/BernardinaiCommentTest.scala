package bro1.commenter.test

import lj.scala.utils.TagSoupFactoryAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}
import bro1.commenter._



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
    
    @Test
    def testBernardinaiWithURLs() {

        val reader = BernardinaiTopicProducer.getReader("file://misc/examples/bernardinai/bernardinai-20110929.html")

        val doc = new TagSoupFactoryAdapter load reader

        val comments = BernardinaiTopicProducer.extractAllComments(doc)

        assertTrue("Comments size should be more than 0", comments.size > 0)

        assertEquals("Number of comments", 10, comments.size)
        assertEquals("Geras Ateistas", (comments{0}.postedBy))
        
        val postedAt = TestUtil.getTimeLT(2011, 8, 30, 2, 32, 0)
        
        assertEquals(postedAt.getTime, (comments{0}.postedAt))
        assertEquals("156524", comments{0}.remoteCommentID)

        val text = comments{0}.text        
        
        assertTrue(text.startsWith(""""S. kad dar nepasirinkes"""))
        assertTrue(text.endsWith("http://blog.lrytas.lt/ateistas"))
        

        // Also check the last comment

        val lastCommentPostedAt = TestUtil.getTimeLT(2011, 8, 29, 22, 54, 0)
        assertEquals("Jahja", (comments{7}.postedBy))
        assertEquals(lastCommentPostedAt.getTime, (comments{7}.postedAt))

      
    }

}


