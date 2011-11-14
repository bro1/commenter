package bro1.commenter.test


import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}
import bro1.commenter._
import bro1.commenter.impl.delfi.DelfiPoster
import java
.net.URL
import org.specs.io.FileReader
import bro1.commenter.impl.bernardinai.BernardinaiPoster
class BernardinaiPosterTest {

  
  @Test
  def testURL() {
    val url = new URL("http://www.labas.lt/katalogas/failas.php?uzklausa=1#23")
    assertEquals("/katalogas/failas.php", url.getPath())
  }
  
  
  @Test
  def textExtractToken() {
    val reader = BernardinaiTopicProducer.getReader("file://misc/examples/bernardinai/bernardinai-comment-form-20111029.html")
    val token = BernardinaiPoster.extractToken(reader)
    assertEquals("b28950a2c8c49db9fe65705634e4e88d", token)
  }
  
    @Test
    def testExampleTest {

      if (false) {
      
      val url = "http://verslas.delfi.lt/Media/lietuvos-lenkai-noretu-ziniu-lenku-kalba.d?id=50839226&com=1"
      
      DelfiPoster.delfiPost(url, "geras ateistas", "", 
"""Gauti informaciją gerai suprantama kalba yra labai puiku! 

blog.lrytas.lt/ateistas""")

      }

    }
       
}
