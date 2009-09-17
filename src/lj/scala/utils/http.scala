package lj.scala.utils

import bro1.mine.http.HttpUtil

object http {

  val http = new HttpUtil;
  
  def download (url : String) = {
    
	  http.getUrl(url);
    
  }
  
}
