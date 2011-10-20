package bro1.commenter.test


import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert._
import java.util.{Calendar, GregorianCalendar, TimeZone}
import bro1.commenter._
import bro1.commenter.impl.delfi.DelfiPoster


class DelfiPosterTest {
    
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
