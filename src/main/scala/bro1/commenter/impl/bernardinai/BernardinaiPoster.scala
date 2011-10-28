package bro1.commenter.impl.bernardinai

import org.apache.http._
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.entity.UrlEncodedFormEntity
import scala.collection.JavaConversions
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.params.ClientPNames
import org.apache.http.client.params.CookiePolicy
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.client.protocol.ClientContext
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.util.EntityUtils
import bro1.commenter.{Poster, Comment, Topic}
import java.io.FileInputStream


import java.io.File
import java.io.FileInputStream


object BernardinaiPoster extends Poster {

  
  protected def properties = {
    val p = new java.util.Properties()
    
    val userHome = System.getProperties().getProperty("user.home")
    val f = new File(userHome + File.separator + ".commenter")
    val fis = new FileInputStream(f)
    p.load(fis)
    (p.getProperty("bernardinai.username"), p.getProperty("bernardinai.password"))
  }
  
  protected def bernardinaiPost(url: String, name : String, email : String, commentText : String) {

    val client: HttpClient = new DefaultHttpClient()
    client.getParams().setParameter(
      ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);

    val cookieStore = new BasicCookieStore()

    client.getParams().setParameter(ClientContext.COOKIE_STORE, cookieStore)

    // TODO: do not commit
    val (a, b) = properties
    
    val httppostLogin = getLogin(a, b)

    val httppost = new HttpPost(url)

    val formFields: List[NameValuePair] =
      new BasicNameValuePair("commit", "1") ::
        new BasicNameValuePair("name", name) ::
        new BasicNameValuePair("email", email) ::
        new BasicNameValuePair("body", commentText) ::
        new BasicNameValuePair("button1.x", "100") ::
        new BasicNameValuePair("button1.y", "10") ::
        Nil

    val formFieldsList = JavaConversions.asList(formFields)

    val entity = new UrlEncodedFormEntity(formFieldsList, "UTF-8")


    httppost.setEntity(entity)
    httppost.setHeader("Referer", url)
    
    val resultForCookie = client.execute(httppostLogin)


    resultForCookie.getEntity().consumeContent()

    val result = client.execute(httppost)
    val resultEntity = result.getEntity()
    val contentAfterPost = EntityUtils.toString(resultEntity)
  }

  def post(t: Topic, c: Comment) {
	  bernardinaiPost(t.url, c.postedBy, c.email, c.text)
  }
  
  
  private def getLogin(user : String, pass : String): org.apache.http.client.methods.HttpPost = {
    
    val formFields: List[NameValuePair] =
      
        new BasicNameValuePair("signin[username]", user) ::
        new BasicNameValuePair("signin[password]", pass) ::
        new BasicNameValuePair("signin[remember]", "on") ::
        Nil

    val formFieldsList = JavaConversions.asList(formFields)

    	val entity = new UrlEncodedFormEntity(formFieldsList, "UTF-8")
    
    val httpPost = new HttpPost("http://www.bernardinai.lt/login")   

    httpPost.setEntity(entity)
    
    httpPost
  }
}