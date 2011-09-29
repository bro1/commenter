package lj.scala.utils

object RepetitiveTasks {
  
  var repeatEvery : Int = 1000; 
 
  def oncePerSecond(callback: () => Unit): Unit =
  {
    while (true)
    {
      callback()
      Thread.sleep(repeatEvery)
    }
  }

}
