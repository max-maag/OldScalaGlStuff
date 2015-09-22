package util

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION

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
    case _ => s"Unknown error: ${code}"
  }
  
  def printIfError() = getError().map{println}
}