package util

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL32
import org.lwjgl.opengl.GL33
import org.lwjgl.opengl.GL11



object GlInfo {
  private val info: GlInfo = new GlInfo
  def minorVersion = info.minorVersion
  def majorVersion = info.majorVersion
  def versionString = info.versionString
  def vendor = info.vendor
  def renderer = info.renderer
  def supportedExtensions = info.supportedExtendsions
  def supportsExtension(e: String) = info.supportedExtendsions.contains(e)
  def supportsExtensions(es: String*) = es.filterNot(info.supportedExtendsions.contains(_)).isEmpty
}

private class GlInfo {
  val versionString = glGetString(GL_VERSION)
  
  val (majorVersion, minorVersion) = {
    val p = """(\d+)\.(\d+).*""".r
    versionString match {
      case p(maj, min) => (maj.toInt, min.toInt)
    }
  }
  
  val vendor = glGetString(GL_VENDOR)
  val renderer = glGetString(GL_RENDERER)
  
  val supportedExtendsions = {
    if(majorVersion < 3)
      glGetString(GL_EXTENSIONS).split(' ').toSet
    else {
      var set = Set.empty[String]
      for(i <- 0 to (GL11.glGetInteger(GL_NUM_EXTENSIONS) - 1)) {
        set = set + glGetStringi(GL_EXTENSIONS, i)
      }
      set
    }
  } 
    
}