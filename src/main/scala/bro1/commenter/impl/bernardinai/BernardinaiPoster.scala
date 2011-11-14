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
import java.io.{FileInputStream, StringReader, File}
import lj.scala.utils.TagSoupFactoryAdapter



object BernardinaiPoster extends Poster {

  protected def getCredentials() = {
    val properties = new java.util.Properties()

    val userHome = System.getProperties().getProperty("user.home")
    val file = new File(userHome + File.separator + ".commenter")
    val inputStream = new FileInputStream(file)
    properties.load(inputStream)
    (properties.getProperty("bernardinai.username"), properties.getProperty("bernardinai.password"))
  }

  protected def bernardinaiPost(url: String, commentText: String) {

    val client: HttpClient = new DefaultHttpClient()
    client.getParams().setParameter(
      ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);

    val cookieStore = new BasicCookieStore()

    client.getParams().setParameter(ClientContext.COOKIE_STORE, cookieStore)

    val (userName, password) = getCredentials
    val httppostLogin = getLogin(userName, password)

    val resultForCookie = client.execute(httppostLogin)
    resultForCookie.getEntity().consumeContent()
    
    
           
    val httppost = getPost(url, commentText)
    
    val httpget = getTokenGetRequest(url)
    val token = getToken(client, httpget)   

    val result = client.execute(httppost)
    val resultEntity = result.getEntity()
    val contentAfterPost = EntityUtils.toString(resultEntity)
  }
  
  
  protected def getToken(client : HttpClient, getRequest : HttpGet) : String = {
    
    val result = client.execute(getRequest)
    val resultEntity = result.getEntity()
    val content = EntityUtils.toString(resultEntity)
    
    val reader = new StringReader(content)    
    extractToken(reader)
  } 
  
  
  def extractToken(reader: java.io.Reader) = {
	  val doc = new TagSoupFactoryAdapter load reader
	  	  
	  val inputs = doc \\ "input"
	  val b = inputs.filter(a => {(a \ "@id").text == "sf_comment_token"})	  
	  b.first.attributes("value").text	  
  }
  

  def post(topic: Topic, comment: Comment) {
    bernardinaiPost(topic.url, comment.text)
  }

  
  private def getTokenGetRequest(url : String) : org.apache.http.client.methods.HttpGet = {
    val httpGet = new HttpGet(url)    
    httpGet
  }
  
  
  
  private def getPost(url : String, commentText : String) : org.apache.http.client.methods.HttpPost = {
    val httppost = new HttpPost(url)

    val formFields: List[NameValuePair] =
      new BasicNameValuePair("commit", "1") ::
        new BasicNameValuePair("sf_comment[text]", commentText) ::
        new BasicNameValuePair("sf_comment[referer]", new java.net.URL(url).getPath()) ::
        new BasicNameValuePair("sf_comment[token]", commentText) ::
        new BasicNameValuePair("commit", "submit") ::
        Nil

    val formFieldsList = JavaConversions.asList(formFields)

    val entity = new UrlEncodedFormEntity(formFieldsList, "UTF-8")

    httppost.setEntity(entity)
    httppost.setHeader("Referer", url)
    
    httppost
  }
  
  
  private def getLogin(user: String, pass: String): org.apache.http.client.methods.HttpPost = {

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