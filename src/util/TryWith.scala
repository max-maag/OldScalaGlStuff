package util

import scala.util.control.NonFatal
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object TryWith {
  def apply [Closable <: { def close() }, Result]
      (resGen: => Closable) (r: (Closable) => Result): Try[Result] = {
    Try(resGen) match {
      case Success(closable) => {
        try Success(r(closable)) catch {
          case NonFatal(e) => Failure(e)
        } finally {
          try {
            closable.close()
          } catch {
            case e: Exception => {
              System.err.println("Failed to close Resource:")
              e.printStackTrace()
            }
          }
        }
      }
      case Failure(e) => Failure(e)
    }
  }
}