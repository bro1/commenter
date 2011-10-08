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
      assertTrue(BernardinaiTopicProducer.accepts(new URL("http://www.bernardinai.lt/aa")))
      assertTrue(BernardinaiTopicProducer.accepts(new URL("https://www.bernardinai.lt/aa")))
      assertFalse(BernardinaiTopicProducer.accepts(new URL("https://www.bernardinas.lt/aa")))
      
      assertTrue(DelfiTopicProducer.accepts(new URL("http://www.delfi.lt/aa")))
      assertTrue(DelfiTopicProducer.accepts(new URL("https://www.delfi.lt/aa")))
      assertFalse(DelfiTopicProducer.accepts(new URL("https://www.delfis.lt/aa")))
    }
    
    @Test
    def testGetBernardinaiTitle() {
      assertEquals("Rima Malickaitė. Didžiausias poreikis – įgyti racionalų tikėjimo pamatą", 
         BernardinaiTopicProducer.getTitle(new URL("file://misc/examples/bernardinai/bernardinai-20110929.html")))
    }
       
}
 