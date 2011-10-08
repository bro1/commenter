package bro1.commenter.test

import java.util.{Calendar, GregorianCalendar, TimeZone}

object TestUtil {
  
  private val tz = java.util.TimeZone.getTimeZone("Europe/Vilnius")  
  
  def getTimeLT(year : Int, month : Int, day : Int, hour : Int, minute : Int, second : Int) = {
    
        val postedAt = new java.util.GregorianCalendar(tz)
        postedAt.set(year, month, day, hour, minute, second)
        postedAt.set(Calendar.MILLISECOND, 0)
        
        postedAt
  	}
}
