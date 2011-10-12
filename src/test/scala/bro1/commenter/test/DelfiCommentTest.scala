package bro1.commenter.test

import lj.scala.utils.TagSoupFactoryAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}
import bro1.commenter._


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
        assertEquals(firstCommentPostedAt.getTime, (firstComment.timeStamp))
        assertEquals("24022150", firstComment.remoteCommentID)

        val text = firstComment.text        

        assertTrue(text.startsWith("godumas,pinigai,noras valdyti"))
        assertTrue(text.endsWith("ir Hitleriui niekas paminklų nestato..."))

        // Also check the last comment

        val lastCommentPostedAt = TestUtil.getTimeLT(2009, 8, 12, 23, 31, 00)

        val lastComment = comments{19}

        assertEquals("to :)", (lastComment.postedBy))
        assertEquals(lastCommentPostedAt.getTime, (lastComment.timeStamp))

    }
       
}
