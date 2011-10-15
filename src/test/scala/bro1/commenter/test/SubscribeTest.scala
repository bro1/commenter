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
      
    	object nameField extends TextField() 
    	object urlField extends TextField()
      
    	TopicHelper.updateName(nameField, urlField)
    	assertEquals("", nameField.text)
      
    }
    
    @Test
    def testIsValidURL {
      object urlField extends TextField()
      assertFalse(TopicHelper.isValidURL(urlField))
      
      urlField.text = "htp"
      assertFalse(TopicHelper.isValidURL(urlField))
      
      urlField.text = "http://sekretore.com"
      assertTrue(TopicHelper.isValidURL(urlField))

    }
    
    @Test
    def testAcceptsBernardinai() {
      val url1 = new URL("http://www.bernardinai.lt/straipsnis/2000-01-01-*/123")
      assertTrue(BernardinaiTopicProducer.accepts(url1))            
      assertEquals(new URL(url1.toString()+ "/comments"), (BernardinaiTopicProducer.matchesPattern(url1).get))
      
      val url2 = new URL("file://www.bernardinai.lt/bernardinai/straipsniai/comments")      
      assertTrue(BernardinaiTopicProducer.accepts(url2))
      assertFalse(BernardinaiTopicProducer.matchesPattern(url2).isDefined)
      
      assertFalse(BernardinaiTopicProducer.accepts(new URL("file://blah")))      
    }
    
    @Test
    def testAcceptsDelfi() {
      val url1 = new URL("http://www.delfi.lt/aa?id=13")          
      assertTrue(DelfiTopicProducer.accepts(url1))      
      assertEquals(new URL(url1 + "&com=1"), DelfiTopicProducer.matchesPattern(url1).get)
      
      val url2 = new URL("http://www.delfi.lt/aa?id=13&com=123")          
      assertTrue(DelfiTopicProducer.accepts(url2))      
      assertEquals(new URL("http://www.delfi.lt/aa?id=13&com=1"), DelfiTopicProducer.matchesPattern(url2).get)
      
      assertFalse(DelfiTopicProducer.accepts(new URL("https://www.delfi.lt/aa")))
      assertFalse(DelfiTopicProducer.accepts(new URL("https://www.delfis.lt/aa")))
    }
    
    @Test
    def testGetBernardinaiTitle() {
      assertEquals("Rima Malickaitė. Didžiausias poreikis – įgyti racionalų tikėjimo pamatą", 
         BernardinaiTopicProducer.getTitle(new URL("file://misc/examples/bernardinai/bernardinai-20110929.html")))
    }
    
    @Test
    def testGetDelfiTitle() {
      assertEquals("Sekmadienio Evangelija. Krikščionybės kilmės vieta", 
         DelfiTopicProducer.getTitle(new URL("file://misc/examples/delfi/delfi-20111013.html")))
    }
    

    @Test
    def testBernardinaiURLPatternMatch() {
      val a = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/70152")
      assertTrue(BernardinaiTopicProducer.matchesPattern(a).isDefined)
      
      
      val b = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/70152/comments")
      assertTrue(BernardinaiTopicProducer.matchesPattern(b).isDefined)
      
      val c = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/abc/comments")
      assertFalse(BernardinaiTopicProducer.matchesPattern(c).isDefined)
      
      val d = new URL("http://www.bernardinai.lt/straipsnis/2011-10-09-rinkeju-apklausa-lenkijoje-rinkimus-laimejo-premjero-d-tusko-pilietine-platforma/123/commentsabc")
      assertFalse(BernardinaiTopicProducer.matchesPattern(d).isDefined)
    }
    

    
       
}
 