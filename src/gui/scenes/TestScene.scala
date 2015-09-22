package gui.scenes

import gui.Scene
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL33._
import org.lwjgl.opengl.GL40._
import org.lwjgl.opengl.GL43._
import util.GLInfo
import java.io.File
import util.ShaderProgram
import util.ShaderFactory
import scala.util.Failure
import scala.util.Success
import org.lwjgl.BufferUtils
import util.GlUtils

class TestScene extends Scene {
  val shaderPath = new File(getClass().getResource("/shaders/mdi_test").toURI())
  
  val indirectData = Array(
      3, 1, 0, 0//,
//      6, 4, 3, 1
  )
  
  val vertexData = Array(
      -0.5f, -0.5f, 0f,
       0.5f, -0.5f, 0f,
         0f,  0.5f, 0f/*,
         
      //--------------
       
       -1f, -1f, 0f,
       1f,  -1f, 0f,
       1f,   1f, 0f,
       
       -1f, -1f, 0f,
        1f,  1f, 0f,
       -1f,  1f, 0f*/
  )
  
  // column major!
  val modelData = Array(
    1f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f/*,
    
    0.1f,    0f,    0f,   0f,
    0f,    0.1f,    0f,   0f,
    0f,      0f,  0.1f,   0f,
    -0.8f, -0.8f,   0f, 0.1f,
    
    0.1f,    0f,   0f,   0f,
    0f,    0.1f,   0f,   0f,
    0f,      0f, 0.1f,   0f,
    -0.8f, 0.8f,   0f, 0.1f,
    
    0.1f,    0f,   0f,   0f,
    0f,    0.1f,   0f,   0f,
    0f,      0f, 0.1f,   0f,
    0.8f, -0.8f,   0f, 0.1f,
    
    0.1f,   0f,   0f,   0f,
    0f,   0.1f,   0f,   0f,
    0f,     0f, 0.1f,   0f,
    0.8f, 0.8f,  0f, 0.1f*/
  )
  
  var prog: ShaderProgram = null
  
  def start(): SceneStatus = {
    // compile shaders
    
    val sf = new ShaderFactory()
       .setShader(GL_VERTEX_SHADER, new File(shaderPath, "vert.glsl"))
       .setShader(GL_FRAGMENT_SHADER, new File(shaderPath, "frag.glsl"))
       .registerAttribute("pos")
       .registerAttribute("model")
       
     sf.buildProgram() match {
       case Success(p) => {
         prog = p
       }
       
       case Failure(e) => {
         return SceneError(e)
       }
    }
    
    sf.cleanUp()
    
    
    
    // push data onto gpu
    
//    val indirectBufferId = glGenBuffers()
//    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectBufferId)
    
    val ibuf = BufferUtils.createIntBuffer(indirectData.length)
    ibuf.put(indirectData).flip()
//    glBufferData(GL_DRAW_INDIRECT_BUFFER, ibuf, GL_STATIC_DRAW)
    
    val vaoID = glGenVertexArrays()
    glBindVertexArray(vaoID)
    
    val vertexBufferId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId)
    
    val vbuf = BufferUtils.createFloatBuffer(vertexData.length)
    vbuf.put(vertexData).flip()
    glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW)
    
    val posAttribLoc = prog.attribLocations("pos")
    
    glVertexAttribPointer(posAttribLoc, 3, GL_FLOAT, false, 0, 0)
    
    
    /*
    val modelBufferId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, modelBufferId)
    
    val mbuf = BufferUtils.createFloatBuffer(modelData.length)
    mbuf.put(modelData).flip()
    glBufferData(GL_ARRAY_BUFFER, mbuf, GL_STATIC_DRAW)
    
    val modAttribLoc = prog.attribLocations("model")
    
    // mat4 attribute = 4 vec4 attributes
    glVertexAttribPointer(modAttribLoc,   3, GL_FLOAT, false, 512, 0)
    glVertexAttribPointer(modAttribLoc+1, 3, GL_FLOAT, false, 512, 128)
    glVertexAttribPointer(modAttribLoc+2, 3, GL_FLOAT, false, 512, 256)
    glVertexAttribPointer(modAttribLoc+3, 3, GL_FLOAT, false, 512, 384)
    
    // one matrix per instance, not per vertex
    glVertexAttribDivisor(modAttribLoc,   1)
    glVertexAttribDivisor(modAttribLoc+1, 1)
    glVertexAttribDivisor(modAttribLoc+2, 1)
    glVertexAttribDivisor(modAttribLoc+3, 1)
    
    */
    //glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0)
    
    glEnableVertexAttribArray(posAttribLoc)
    /*
    glEnableVertexAttribArray(modAttribLoc)
    glEnableVertexAttribArray(modAttribLoc+1)
    glEnableVertexAttribArray(modAttribLoc+2)
    glEnableVertexAttribArray(modAttribLoc+3)
    */
    glUseProgram(prog.id)
    
    // DRAW!
    
    glMultiDrawArraysIndirect(GL_TRIANGLES, ibuf, indirectData.length/4, 4)
    
    
    GlUtils.printIfError()
    
    // That's it, clean up time
    
    glUseProgram(0)
    prog.cleanUp()
    /*
    glDisableVertexAttribArray(modAttribLoc)
    glDisableVertexAttribArray(modAttribLoc+1)
    glDisableVertexAttribArray(modAttribLoc+2)
    glDisableVertexAttribArray(modAttribLoc+3)
    */
    glDisableVertexAttribArray(posAttribLoc)
    
    glBindVertexArray(0)
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0)
    
//    glDeleteBuffers(modelBufferId)
    glDeleteBuffers(vertexBufferId)
//    glDeleteBuffers(indirectBufferId)
    
    // Done!
    
    SceneSuccess
  }
  
  def update(dt: Float): SceneResult = {
    
    SceneSuccess
  }
  
  def end() = {
    SceneSuccess
  }
}