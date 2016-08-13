package gui.scenes

import gui.Scene
import util.ShaderProgram
import model.GlPersistentBuffer
import java.io.File
import util.GlUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL32._
import model.Implicits._
import scala.util.Failure
import util.ShaderFactory
import math.Vec2f
import scala.util.Success
import model.ModelManager
import model.RenderManager
import util.GlInfo
import model.ModelManager
import org.lwjgl.BufferUtils
import model.GameObject
import model.Transform2D
import model.Transform2D
import model.Triangles
import util.FragmentShader
import util.VertexShader

/* TODO:
 * 1. Find out why the screen is black.
 * 2. Create traits for indirect rendering, instanced rendering and standard vbo rendering.
 * 		Then create renderers that render objects with these traits.
 * 
 * 		IndirectRenderable for example would require the current GameObject.uniformData method and
 * 		would be rendered by current RenderManager.
 */
class RenderManagerScene extends Scene {
  val shaderPath = new File(getClass().getResource("/shaders/mdi2d_test").toURI())
  
  var modelManager: ModelManager[String] = null
  var renderManager: RenderManager = null
  var prog: ShaderProgram = null
  var vaoId = 0
  var gameObjects = List.empty[GameObject[String]]
  
  override def start(): SceneStatus = {
    if(!isSupported)
      return SceneError(new RuntimeException(
          "Sorry, you need a graphics card that supports OpenGL 4.4 or at least the ARB_multi_draw_indirect and ARB_buffer_storage extensions."))
          
    val sf = new ShaderFactory()
      .setShader(VertexShader, new File(shaderPath, "vert.glsl"))
      .setShader(FragmentShader, new File(shaderPath, "frag.glsl"))
      .registerAttribute("pos")
      .registerAttribute("model", 3)
    
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
    println("Compiled shaders")
    
    modelManager = new ModelManager(1024, 1024)
    GlUtils.printIfError()
    println("Created model manager")
    renderManager = new RenderManager(128, 1024)
    GlUtils.printIfError()
    println("Created render manager")
    
    loadModels()
    GlUtils.printIfError()
    println("Loaded models")
    createObjects()
    GlUtils.printIfError()
    println("Created game objects")
    
    vaoId = glGenVertexArrays()
    glBindVertexArray(vaoId)
    GlUtils.printIfError()
    println("Created vao")
    
    modelManager.bindBuffers()
    GlUtils.printIfError()
    println("Bound model manager buffers")
    
    // model vertex array pointer
    val posAttribLoc = prog.attributes("pos").location
    glVertexAttribPointer(posAttribLoc, 2, GL_FLOAT, false, 0, 0)
    GlUtils.printIfError()
    println("Set pos attrib pointers")
    
    renderManager.bindBuffers()
    
    val modAttribLoc = prog.attributes("model").location
    
    val bytesPerFloat = 4
    val floatsPerColumn = 3
    val floatsPerMat = floatsPerColumn * floatsPerColumn
    val stride = bytesPerFloat * floatsPerMat
    
    // mat3 attribute = 3 vec3 attributes
    for(i <- 0 to 2)
      glVertexAttribPointer(modAttribLoc+i, 3, GL_FLOAT, false, stride, i*floatsPerColumn*bytesPerFloat)
      
      
    GlUtils.printIfError()
    println("set model attrib pointers")
    
    // one matrix per instance, not per vertex
    prog.vertexAttribDivisor("model", 1)
    GlUtils.printIfError()
    println("Set model attrib divisor")
    prog.enableVertAttribArrays()
    GlUtils.printIfError()
    println("Enabled vertex attrib arrays")
    prog.use()
    GlUtils.printIfError()
    println("Enabled shaders")
    
    SceneSuccess
  }
  
  private def loadModels() = {
    val buffer = BufferUtils.createByteBuffer(32)
    buffer.putFloat(-1)
      .putFloat(-1)
      .putFloat(1)
      .putFloat(-1)
      .putFloat(0)
      .putFloat(1)
      .flip()
    
    val iBuffer = BufferUtils.createByteBuffer(24)
    iBuffer
      .putInt(0)
      .putInt(1)
      .putInt(2)
      .flip()
    
    modelManager.putModel("triangle", buffer, iBuffer)
    
    buffer.clear()
    buffer.putFloat(-1)
      .putFloat(-1)
      .putFloat(1)
      .putFloat(-1)
      .putFloat(1)
      .putFloat(1)
      .putFloat(-1)
      .putFloat(1)
      .flip()
    
    iBuffer.clear()
    iBuffer.putInt(0)
      .putInt(1)
      .putInt(2)
      .putInt(0)
      .putInt(2)
      .putInt(3)
      .flip()
      
    modelManager.putModel("quad", buffer, iBuffer)
  }
  
  private def createObjects() = {
    gameObjects ++= List(
        new Transform2D("quad", new Vec2f(), 0, 0.2f),
        new Transform2D("triangle", new Vec2f(-0.8f, -0.8f), 0, 0.1f),
        new Transform2D("triangle", new Vec2f(0.8f, -0.8f), 0, 0.1f),
        new Transform2D("triangle", new Vec2f(0.8f, 0.8f), 0, 0.1f),
        new Transform2D("triangle", new Vec2f(-0.8f, 0.8f), 0, 0.1f))
  }
  
  override def update(dt: Float) = {
    renderManager.render(gameObjects, modelManager, Triangles)
    GlUtils.printIfError()
    SceneSuccess
  }
  
  override def end() = {
    renderManager.cleanUp()
    modelManager.cleanUp()
    prog.cleanUp()
    SceneSuccess
  }
  
  def isSupported =
    GlInfo.majorVersion >= 4 && GlInfo.minorVersion >= 4 ||
    GlInfo.supportsExtensions("GL_ARB_multi_draw_indirect", "GL_ARB_buffer_storage")
}