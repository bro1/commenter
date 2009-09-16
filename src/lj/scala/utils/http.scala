package lj.scala.utils

import bro1.mine.http.HttpUtil

object http {

  val httpUtil = new HttpUtil()
  
  HttpUtil.setDebugMode(true)
  
  def download (url : String) = {
    
	  httpUtil.getUrl(url);
    
  }
  
}
