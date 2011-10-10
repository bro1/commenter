package bro1.commenter.test

import lj.scala.utils.TagSoupFactoryAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}
import bro1.commenter._
import scala.swing.TextField
import java.net.URL


class SubscribeTest {
    
    @Test
    def testBothEmpty {
      
    	object n extends TextField() 
    	object u extends TextField()
      
    	TopicHelper.updateName(n, u)
    	assertEquals("", n.text)
      
    }
    
    @Test
    def testIsValidURL {
      object u extends TextField()
      assertFalse(TopicHelper.isValidURL(u))
      
      u.text = "htp"
      assertFalse(TopicHelper.isValidURL(u))
      
      u.text = "http://sekretore.com"
      assertTrue(TopicHelper.isValidURL(u))

    }
    
    @Test
    def testAccepts() {
      assertTrue(BernardinaiTopicProducer.accepts(new URL("http://www.bernardinai.lt/straipsnis/2000-01-01-*/123")))
      assertTrue(BernardinaiTopicProducer.accepts(new URL("file://www.bernardinai.lt/bernardinai/straipsniai/comments")))
      assertFalse(BernardinaiTopicProducer.accepts(new URL("file://blah")))
      
      
      assertTrue(DelfiTopicProducer.accepts(new URL("http://www.delfi.lt/aa")))
      assertTrue(DelfiTopicProducer.accepts(new URL("https://www.delfi.lt/aa")))
      assertFalse(DelfiTopicProducer.accepts(new URL("https://www.delfis.lt/aa")))
    }
    
    @Test
    def testGetBernardinaiTitle() {
      assertEquals("Rima Malickaitė. Didžiausias poreikis – įgyti racionalų tikėjimo pamatą", 
         BernardinaiTopicProducer.getTitle(new URL("file://misc/examples/bernardinai/bernardinai-20110929.html")))
    }

    @Test
    def testBernardinaiURLPatternMatch() {
      val a = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/70152")
      assertTrue(BernardinaiTopicProducer.matchesPattern(a))
      
      
      val b = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/70152/comments")
      assertTrue(BernardinaiTopicProducer.matchesPattern(b))
      
      val c = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/abc/comments")
      assertFalse(BernardinaiTopicProducer.matchesPattern(c))
      
      val d = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/123/commentsabc")
      assertFalse(BernardinaiTopicProducer.matchesPattern(d))
    }
    

    
       
}
 