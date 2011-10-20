package bro1.commenter

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

abstract class Poster {
  def post(topic: Topic, comment: Comment)
}
