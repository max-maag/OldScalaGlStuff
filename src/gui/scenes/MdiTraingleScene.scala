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

class MdiTriangleScene extends Scene {
  val shaderPath = new File(getClass().getResource("/shaders/mdi_test").toURI())
  
  val indirectData = Array(
      3, 1, 0, 0
  )
  
  val vertexData = Array(
      -0.5f, -0.5f, 0f,
       0.5f, -0.5f, 0f,
         0f,  0.5f, 0f
  )
  
  val ibuf = BufferUtils.createIntBuffer(indirectData.length)
  ibuf.put(indirectData).flip()
  
  var prog: ShaderProgram = null
  var vboId = 0
  var vaoId = 0
  
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
         sf.cleanUp()
         return SceneError(e)
       }
    }
    
    sf.cleanUp()
    
    
    // push data onto gpu
    
    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)
    
    vboId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    
    val vbuf = BufferUtils.createFloatBuffer(vertexData.length)
    vbuf.put(vertexData).flip()
    glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW)
    
    val posAttribLoc = prog.attribLocations("pos")
    
    glVertexAttribPointer(posAttribLoc, 3, GL_FLOAT, false, 0, 0)
    
    glEnableVertexAttribArray(posAttribLoc)
    
    glUseProgram(prog.id)
    
    SceneSuccess
  }
  
  def update(dt: Float): SceneResult = {
    // draw
    glMultiDrawArraysIndirect(GL_TRIANGLES, ibuf, indirectData.length/4, 0)
    GlUtils.printIfError()
    
    SceneSuccess
  }
  
  def end() = {
    // cleanup
    glUseProgram(0)
    prog.cleanUp()
    
    glDisableVertexAttribArray(prog.attribLocations("pos"))
    
    glBindVertexArray(0)
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0)
    
    glDeleteBuffers(vboId)
    SceneSuccess
  }
}