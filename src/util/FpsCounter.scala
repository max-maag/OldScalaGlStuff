package util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FpsCounter {
  val fpsUpdateDt = 500 // ms
  var frames = 0
  var fps = 0f
  var stop = false
  
  def start(): Unit = {
    val f = Future {
      val start = System.currentTimeMillis()
      while(System.currentTimeMillis() - start < fpsUpdateDt) {
        try {
          Thread.sleep(fpsUpdateDt)
        } catch {
          case e: InterruptedException =>
        }
      }
      
      fps = frames.toFloat / (System.currentTimeMillis() - start) * 1000f
      frames = 0
      println(fps.round)
    }
    f.onComplete { _ => if(!stop) start() }
  }
  
  def update() = frames += 1
}