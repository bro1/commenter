package bro1.commenter.test

import lj.scala.utils.TagSoupFactoryAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}
import bro1.commenter._
import bro1.commenter.impl.delfi.DelfiTopicProducer


class DelfiCommentTest {
    
    @Test
    def testExampleTest {

        val reader = DelfiTopicProducer.getReader("file://misc/examples/delfi/delfi-20111013.html")

        val doc = new TagSoupFactoryAdapter load reader

        val comments = DelfiTopicProducer.extractAllComments(doc)

        assertTrue("Comments size should be more than 0", comments.size > 0)

        assertEquals("Number of comments does not match", 20, comments.size)

        val firstComment = comments{0}
        

        assertEquals("Kam nesuprantama - tie ir neprieina prie Šaltinio.", (firstComment.postedBy))
        val firstCommentPostedAt = TestUtil.getTimeLT(2009, 8, 19, 18, 25, 00)
        assertEquals(firstCommentPostedAt.getTime, (firstComment.timeStamp))
        assertEquals("24128739", firstComment.remoteCommentID)

        val text = firstComment.text        

        println(text)
        
        assertTrue(text.startsWith("šią minutę versmė mena"))
        assertTrue(text.endsWith("poreikių."))

        // Also check the last comment

        val lastCommentPostedAt = TestUtil.getTimeLT(2009, 8, 19, 11, 31, 00)

        val lastComment = comments{19}

        assertEquals("""pastebiu:išnaudojamas žmogus sako "gana".""", (lastComment.postedBy))
        assertEquals(lastCommentPostedAt.getTime, (lastComment.timeStamp))
        
        
        val multilineComment = comments{3}
        val multilineCommentText = multilineComment.text        
        assertTrue(multilineCommentText.startsWith("Klausimas: "))
        assertTrue(multilineCommentText.endsWith("Atsakymas: pagal paskirtį.\n\n."))

    }
       
}
