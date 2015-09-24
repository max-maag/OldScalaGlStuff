package gui.scenes

import gui.Scene
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL33._
import org.lwjgl.opengl.GL40._
import org.lwjgl.opengl.GL43._
import java.io.File
import util.ShaderProgram
import util.ShaderFactory
import scala.util.Failure
import scala.util.Success
import org.lwjgl.BufferUtils
import util.GlUtils
import org.lwjgl.opengl.GL31
import util.GlInfo

class MdiScene extends Scene {
  val shaderPath = new File(getClass().getResource("/shaders/mdi_test").toURI())
  
  // Data for the indirect buffer
  val indirectData = Array(
      3, 1, 0, 0,
      6, 4, 3, 1
  )
  
  val vertexData = Array(
      // a centered triangle
      
      -0.5f, -0.5f, 0f,
       0.5f, -0.5f, 0f,
         0f,  0.5f, 0f,
         
      //--------------
       
       // a quad (too big for the screen without scaling)
         
       -1f, -1f, 0f,
       1f,  -1f, 0f,
       1f,   1f, 0f,
       
       -1f, -1f, 0f,
        1f,  1f, 0f,
       -1f,  1f, 0f
  )
  
  // model transformation data
  // column major!
  val modelData = Array(
    // identity matrix for the triangle
      
    1f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f,
    
    // four matrices for the quads   
    // that scale and translate them
    
    0.1f,    0f,    0f,   0f,
    0f,    0.1f,    0f,   0f,
    0f,      0f,  0.1f,   0f,
    -0.8f, -0.8f,   0f,   1f,
    
    0.1f,    0f,   0f,   0f,
    0f,    0.1f,   0f,   0f,
    0f,      0f, 0.1f,   0f,
    -0.8f, 0.8f,   0f,   1f,
    
    0.1f,    0f,   0f,   0f,
    0f,    0.1f,   0f,   0f,
    0f,      0f, 0.1f,   0f,
    0.8f, -0.8f,   0f,   1f,
    
    0.1f,   0f,   0f,   0f,
    0f,   0.1f,   0f,   0f,
    0f,     0f, 0.1f,   0f,
    0.8f, 0.8f,   0f,   1f
  )
  
  var prog: ShaderProgram = null
  var vaoId = 0
  var vboId = 0
  var mboId = 0
  var iboId = 0
  
  def start(): SceneStatus = {
    println("Starting")
    
    if(!isSupported())
      return SceneError(new RuntimeException("Sorry, your computer doesn't support the necessary OpenGL features."))
    
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
    
    val iboId = glGenBuffers()
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, iboId)
    
    val ibuf = BufferUtils.createIntBuffer(indirectData.length)
    ibuf.put(indirectData).flip()
    glBufferData(GL_DRAW_INDIRECT_BUFFER, ibuf, GL_STATIC_DRAW)
    
    
    // the vao is not necessary for this example
    // but for the sake of demonstration I'm still using one
    
    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)
    
    val vertexBufferId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId)
    
    val vbuf = BufferUtils.createFloatBuffer(vertexData.length)
    vbuf.put(vertexData).flip()
    glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW)
    
    val posAttribLoc = prog.attribLocations("pos")
    
    glVertexAttribPointer(posAttribLoc, 3, GL_FLOAT, false, 0, 0)
    
    
    
    mboId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, mboId)
    
    val mbuf = BufferUtils.createFloatBuffer(modelData.length)
    mbuf.put(modelData).flip()
    glBufferData(GL_ARRAY_BUFFER, mbuf, GL_STATIC_DRAW)
    
    val modAttribLoc = prog.attribLocations("model")
    
    val bytesPerFloat = 4
    val floatsPerMat = 16
    val stride = bytesPerFloat * floatsPerMat
    
    // mat4 attribute = 4 vec4 attributes
    glVertexAttribPointer(modAttribLoc,   4, GL_FLOAT, false, stride, 0)
    glVertexAttribPointer(modAttribLoc+1, 4, GL_FLOAT, false, stride, 4*bytesPerFloat)
    glVertexAttribPointer(modAttribLoc+2, 4, GL_FLOAT, false, stride, 8*bytesPerFloat)
    glVertexAttribPointer(modAttribLoc+3, 4, GL_FLOAT, false, stride, 12*bytesPerFloat)
    
    // one matrix per instance, not per vertex
    glVertexAttribDivisor(modAttribLoc,   1)
    glVertexAttribDivisor(modAttribLoc+1, 1)
    glVertexAttribDivisor(modAttribLoc+2, 1)
    glVertexAttribDivisor(modAttribLoc+3, 1)
    
    
    
    glEnableVertexAttribArray(posAttribLoc)
    
    glEnableVertexAttribArray(modAttribLoc)
    glEnableVertexAttribArray(modAttribLoc+1)
    glEnableVertexAttribArray(modAttribLoc+2)
    glEnableVertexAttribArray(modAttribLoc+3)
    
    glUseProgram(prog.id)
    
    GlUtils.printIfError()
    println("Start done")
    
    SceneSuccess
  }
  
  def update(dt: Float): SceneResult = {
    // draw, with a single draw call!
    glMultiDrawArraysIndirect(GL_TRIANGLES, 0, indirectData.length/4, 16)
    
    GlUtils.printIfError()
    
    SceneSuccess
  }
  
  def end() = {
    println("Ending")
    
    glUseProgram(0)
    prog.cleanUp()
    
    val modAttribLoc = prog.attribLocations("model")
    
    glDisableVertexAttribArray(modAttribLoc)
    glDisableVertexAttribArray(modAttribLoc+1)
    glDisableVertexAttribArray(modAttribLoc+2)
    glDisableVertexAttribArray(modAttribLoc+3)
    
    glDisableVertexAttribArray(prog.attribLocations("pos"))
    
    glBindVertexArray(0)
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0)
    
    glDeleteVertexArrays(vaoId)
    
    glDeleteBuffers(mboId)
    glDeleteBuffers(vboId)
    glDeleteBuffers(iboId)
    
    GlUtils.printIfError()
    println("End done")
    
    // Done!
    SceneSuccess
  }
  
  def isSupported() =
    GlInfo.majorVersion >= 4 && GlInfo.minorVersion >= 3 ||
    GlInfo.supportsExtension("GL_ARB_multi_draw_indirect")
}