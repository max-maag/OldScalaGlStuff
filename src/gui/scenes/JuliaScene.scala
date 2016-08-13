package gui.scenes

import gui.Scene
import java.io.File
import util.ShaderFactory
import scala.util.Failure
import scala.util.Success
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL42._
import org.lwjgl.opengl.GL43._
import org.lwjgl.BufferUtils._
import util.ShaderProgram
import util.ComputeShader
import util.FragmentShader
import util.VertexShader
import util.GlUtils
import java.nio.IntBuffer

class JuliaScene(width: Int, height: Int) extends Scene {
  private var compProg: ShaderProgram = null
  private var renderProg: ShaderProgram = null
  private var tex = 0
  
  class Complex(val real: Float, val imaginary: Float)
  implicit def tup2complex(t: (Float, Float)) = new Complex(t._1, t._2)
  implicit def itup2complex(t: (Int, Int)) = new Complex(t._1, t._2)
  
  class CompArgs(
      val prog: ShaderProgram,
      val width: Int,
      val height: Int,
      val maxIterations: Int,
      val min: Complex,
      val max: Complex,
      val textureNeedsResize: Boolean = false,
      val fsaa: Int = 1) {
    
    def withProg(p: ShaderProgram) = new CompArgs(p, width, height, maxIterations, min, max, textureNeedsResize, fsaa)
    def withWidth(w: Int) = new CompArgs(prog, w, height, maxIterations, min, max, textureNeedsResize, fsaa)
    def withHeight(h: Int) = new CompArgs(prog, width, h, maxIterations, min, max, textureNeedsResize, fsaa)
    def withMaxIterations(i: Int) = new CompArgs(prog, width, height, i, min, max, textureNeedsResize, fsaa)
    def withMin(m: Complex) = new CompArgs(prog, width, height, maxIterations, m, max, textureNeedsResize, fsaa)
    def withHeight(m: Complex) = new CompArgs(prog, width, height, maxIterations, min, m, textureNeedsResize, fsaa)
    def withTextureNeedsResize(t: Boolean) = new CompArgs(prog, width, height, maxIterations, min, max, t, fsaa)
    def withFsaa(f: Int) = new CompArgs(prog, width, height, maxIterations, min, max, textureNeedsResize, f)
  }
      
  private val compArgsSyncObject = new Object
  private var compArgs: CompArgs = new CompArgs(null, width, height, 1024, (-1, -1), (1, 1))
  private var updatedCompArgs: Option[CompArgs] = None
  
  def start(): SceneStatus = {
    val shaderPath = new File(getClass().getResource("/shaders/julia").toURI())
    val sf = new ShaderFactory()
      .setShader(ComputeShader, new File(shaderPath, "comp.glsl"))
      .registerUniform("img")
      .registerUniform("maxIterations")
      .registerUniform("minVal")
      .registerUniform("maxVal")
    
    sf.buildProgram() match {
      case Success(p) => {
        compProg = p
        sf.cleanUp()
          .clearShaders()
        
        sf
          .setShader(VertexShader, new File(shaderPath, "vert.glsl"))
          .setShader(FragmentShader, new File(shaderPath, "frag.glsl"))
          .registerAttribute("pos")
          .registerUniform("tex")
          .registerUniform("maxIterations")
          .buildProgram() match {
            case Success(p) => {
              renderProg = p
              sf.cleanUp()
            }
            
            case Failure(e) => {
              sf.cleanUp()
              return SceneError(e)
            }
        }
          
      }
     
      case Failure(e) => {
        sf.cleanUp()
        return SceneError(e)
      }
    }
    
    tex = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, tex);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
    
    glClearColor(1f, 0f, 1f, 1f)
    
    renderProg.use()
    compArgsSyncObject.synchronized {
      computeTexture(compArgs.withProg(compProg).withTextureNeedsResize(true))
    }

    glUniform1i(renderProg.uniforms("tex").location, 0)
    glUniform1ui(renderProg.uniforms("maxIterations").location, compArgs.maxIterations)
    
    val vboId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    glBufferData(GL_ARRAY_BUFFER, Array(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f), GL_STATIC_DRAW)
    
    glVertexAttribPointer(renderProg.attributes("pos").location, 2, GL_FLOAT, false, 0, 0)
    renderProg.enableVertAttribArrays()
    
    SceneSuccess
  }
  
  def computeTexture(args: CompArgs) = {
    val fw = args.width * args.fsaa
    val fh = args.height * args.fsaa
    if(args.textureNeedsResize) {
      glTexImage2D(GL_TEXTURE_2D, 0, GL_R32UI, fw, fh, 0, GL_RED_INTEGER, GL_UNSIGNED_INT, 0l)
      glBindImageTexture(0, tex, 0, false, 0, GL_READ_WRITE, GL_R32UI)
    }
    
    compArgsSyncObject.synchronized(compArgs = args)
    val curProg = glGetInteger(GL_CURRENT_PROGRAM)
    
    args.prog.use()
    glUniform1i(args.prog.uniforms("img").location, 0)
    glUniform1ui(args.prog.uniforms("maxIterations").location, args.maxIterations)
    glUniform2f(args.prog.uniforms("minVal").location, args.min.real, args.min.imaginary)
    glUniform2f(args.prog.uniforms("maxVal").location, args.max.real, args.max.imaginary)
    glDispatchCompute(fw, fh, 1)
    
    glUseProgram(curProg)
    
    glMemoryBarrier(GL_TEXTURE_FETCH_BARRIER_BIT)
  }
  
  def update(dt: Float) = {
    compArgsSyncObject.synchronized {
      if(updatedCompArgs.isDefined) {
        computeTexture(updatedCompArgs.get)
        glUniform1ui(renderProg.uniforms("maxIterations").location, compArgs.maxIterations)
        updatedCompArgs = None
      }
    }
    
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    SceneSuccess
  }
  
  override def changeResolution(width: Int, height: Int) = compArgsSyncObject.synchronized {
    updatedCompArgs = Some(compArgs.withWidth(width).withHeight(height).withTextureNeedsResize(true))
  }
  
  def end() = {
    glDeleteTextures(tex)
    
    if(compProg != null)
      glDeleteProgram(compProg.id)
    
    if(renderProg != null)
      glDeleteProgram(renderProg.id)
      
    SceneSuccess
  }
}