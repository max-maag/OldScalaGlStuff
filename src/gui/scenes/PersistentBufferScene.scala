package gui.scenes

import java.io.File
import scala.util.Failure
import scala.util.Success
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL32._
import gui.Scene
import math.Vec2f
import model.GlPersistentBuffer
import model.Implicits._
import util.GlUtils
import util.ShaderFactory
import util.ShaderProgram
import util.VertexShader
import util.FragmentShader

class PersistentBufferScene extends Scene {
  val shaderPath = new File(getClass().getResource("/shaders/simple").toURI())
  
  var prog: ShaderProgram = null
  var buffer: GlPersistentBuffer = null
  var syncId = 0l
  
  def start(): SceneStatus = {
    val sf = new ShaderFactory()
      .setShader(VertexShader, new File(shaderPath, "vert.glsl"))
      .setShader(FragmentShader, new File(shaderPath, "frag.glsl"))
      .registerAttribute("pos")
    
    sf.buildProgram() match {
      case Success(p) => {
        prog = p
        sf.cleanUp()
      }
     
      case Failure(e) => {
        sf.cleanUp()
        return SceneError(e)
      }
    }
  
    GlUtils.printIfError()
    
    buffer = new GlPersistentBuffer(6*2*4)
      
    buffer.buffer.put(new Vec2f(-0.5f, -0.5f))
      .put(new Vec2f(0.5f, -0.5f))
      .put(new Vec2f(-0.5f, 0.5f))
      
      .put(new Vec2f(0.5f, -0.5f))
      .put(new Vec2f(0.5f, 0.5f))
      .put(new Vec2f(-0.5f, 0.5f))
      
    glBindBuffer(GL_ARRAY_BUFFER, buffer.id)
    glVertexAttribPointer(prog.attributes("pos").location, 2, GL_FLOAT, false, 0, 0)
    
    syncId = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
    
    prog.use()
    prog.enableVertAttribArrays()
    
    SceneSuccess
  }
  
  def update(dt: Float) = {
    val c = Math.sin((System.currentTimeMillis()/4 % 1000).toFloat / 1000f * 2*Math.PI - Math.PI).toFloat * 0.5f
    
    GlUtils.checkSyncResult(glClientWaitSync(syncId, GL_SYNC_FLUSH_COMMANDS_BIT, 1000000))
    
    buffer.buffer.clear()
    buffer.buffer
      .put(new Vec2f(-0.5f+c, -0.5f))
      .put(new Vec2f(0.5f+c, -0.5f))
      .put(new Vec2f(-0.5f+c, 0.5f))
      
      .put(new Vec2f(0.5f+c, -0.5f))
      .put(new Vec2f(0.5f+c, 0.5f))
      .put(new Vec2f(-0.5f+c, 0.5f))
      
    glDrawArrays(GL_TRIANGLES, 0, 6)
    
    glDeleteSync(syncId)
    syncId = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
    
    SceneSuccess
  }
  
  def end() = {
    glUseProgram(0)
    prog.disableVertAttribArrays()
    prog.cleanUp()
    buffer.cleanUp()
    
    SceneSuccess
  }
}