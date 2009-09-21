import scala.dbc._
import scala.dbc.syntax._
import scala.dbc.syntax.Statement._
import scala.dbc.statement._
import scala.dbc.value._
import java.net.URI



object SubscribeToTopic {
  
  def main(args: Array[String]) {
    dbctest
  }

  
  
}

object SqliteVendor extends Vendor {
  val uri = new URI("jdbc:sqlite:misc/test.db")
  val user = ""
  val pass = ""
  
  val retainedConnections = 1
  val nativeDriverClass = Class.forName("org.sqlite.JDBC")
  val urlProtocolString = "jdbc:sqlite:"
  
}

object typ {
  def char(v : String) = {Utilities.valueToConstant( new CharacterVarying {
    val dataType = DataTypeUtil.characterVarying(255)
    val nativeValue = v
  })}
  
  def int (v : Int) = {
    Utilities.valueToConstant( new ExactNumeric[int] {
    val dataType = DataTypeUtil.integer
    val nativeValue = v
  })
  }
  
}

object dbctest  {
  val db = new Database(SqliteVendor)
   
  val data = InsertionData.Constructor(
    Some(List("name", "topictype", "url")), 
    List(typ char "Linas" , typ char "Linas2" , typ char "Linas3" ))
 
  db executeStatement  Insert ("topic", data)
}
