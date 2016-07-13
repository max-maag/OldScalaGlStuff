package util

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION
import org.lwjgl.opengl.GL32._

object GlUtils {
  def getError() = glGetError() match {
    case GL_NO_ERROR => None
    case e => Some(getErrorName(e))
  }
  
  def getErrorName(code: Int) = code match {
    case GL_NO_ERROR => "GL_NO_ERROR"
    case GL_INVALID_ENUM => "GL_INVALID_ENUM"
    case GL_INVALID_VALUE => "GL_INVALID_VALUE"
    case GL_INVALID_OPERATION => "GL_INVALID_OPERATION"
    case GL_INVALID_FRAMEBUFFER_OPERATION => "GL_INVALID_FRAMEBUFFER_OPERATION"
    case GL_OUT_OF_MEMORY => "GL_OUT_OF_MEMORY"
    case GL_STACK_UNDERFLOW => "GL_STACK_UNDERFLOW"
    case GL_STACK_OVERFLOW => "GL_STACK_OVERFLOW"
    case _ => s"Unknown error: $code"
  }
  
  def printIfError(): Unit = getError().map{println}
  
  def checkSyncResult(res: Int): Unit = res match {
    case GL_ALREADY_SIGNALED =>
    case GL_CONDITION_SATISFIED =>
    case GL_TIMEOUT_EXPIRED => println("Timeout expired")
    case GL_WAIT_FAILED =>
      println("Wait failed")
      GlUtils.printIfError()
      
    case i =>
      println("Unknown sync result: " + i)
      GlUtils.printIfError()
  }
}