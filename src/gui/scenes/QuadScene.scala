package gui.scenes

import gui.Scene
import util.ShaderProgram
import util.ShaderFactory
import scala.util.Success
import scala.util.Failure
import java.io.File
import org.lwjgl.opengl.GL20._
import java.io.IOException
import java.net.URL
import util.GlUtils
import util.GLInfo
import org.lwjgl.opengl.GL11
import model.Rect

class QuadScene extends Scene {
  var initError: Option[Throwable] = None
  
  val shaderUrl = getClass().getResource("/shaders/simple") match {
    case null => {
      initError = Some(new IOException("Unable to locate shaders."))
      None
    }
    case v => Some(v)
  }
  
  val shaderPath = shaderUrl.map{url: URL => new File(url.toURI())}
  
  var r: Option[Rect] = None;
  var prog: Option[ShaderProgram] = None;
  
  def start(): SceneStatus = {
    if(initError.isDefined)
      return SceneError(initError.get)
      
     val sf = new ShaderFactory()
       .setShader(GL_VERTEX_SHADER, new File(shaderPath.get, "vert.glsl"))
       .setShader(GL_FRAGMENT_SHADER, new File(shaderPath.get, "frag.glsl"))
       .setAttribLocation("pos", 0)
       
     sf.buildProgram() match {
       case Success(p) => {
         prog = Some(p)
         r = Some(new Rect((0f, 0f), 0.5f, 0.5f))
       }
       
       case Failure(e) => {
         e.printStackTrace()
         end()
       }
     }
    
     sf.cleanUp()
     
     SceneSuccess
  }
  
  def update(dt: Float): SceneResult = {
    if(initError.isDefined)
      return SceneError(initError.get)
      
    prog.get.use()
    GlUtils.printIfError()
    r.get.render()
    GlUtils.printIfError()
    
    SceneSuccess
  }
  
  def end() = {
    println("QuadScene end")
    r.foreach(_.cleanUp())
    prog.foreach(_.cleanUp())
    
    SceneSuccess
  }
}