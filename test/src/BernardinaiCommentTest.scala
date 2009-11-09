/*
 * BernardinaiCommentTest.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._

import lj.scala.utils.TagSoupFactoryAdapter

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
        val postedAt = new java.util.GregorianCalendar(2009, 10, 6, 11, 05, 00)
        assertEquals(postedAt.getTime, (comments{0}.postedAt))
        assertEquals("100739", comments{0}.remoteCommentID)

        // Also check the last comment

        val lastCommentPostedAt = new java.util.GregorianCalendar(2009, 10, 5, 10, 14, 00)
        assertEquals("drager", (comments{7}.postedBy))
        assertEquals(lastCommentPostedAt.getTime, (comments{7}.postedAt))

    }

}
