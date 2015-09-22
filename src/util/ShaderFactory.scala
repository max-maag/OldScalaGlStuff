package util

import java.io.File
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11
import scala.io.Source
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class ShaderFactory {
  private var shaderIds = Map.empty[File, Int]
  private var shaders = Map.empty[Int, File]
  private var attribLocations = Map.empty[String, Int]
  private var uniformLocations = Map.empty[String, Int]
  
  def setShader(shaderType: Int, path: File): ShaderFactory = {
    shaders += shaderType -> path
    return this
  }
  
  def setAttribLocation(name: String, pos: Int): ShaderFactory = {
    attribLocations += name -> pos
    return this
  }
  
  def unsetAttribute(name: String): ShaderFactory = {
    attribLocations -= name
    return this
  }
  
  def registerAttribute(name: String): ShaderFactory = {
    attribLocations += name -> ShaderFactory.UNKNOWN_LOCATION
    return this
  }
  
  def registerUniform(name: String): ShaderFactory = {
    uniformLocations += name -> ShaderFactory.UNKNOWN_LOCATION
    return this
  }
  
  def buildProgram(): Try[ShaderProgram] = {
    val pId = glCreateProgram()
    for(entry <- shaders) {
      if(!shaderIds.contains(entry._2)) {
        compileShader(entry._1, entry._2) match {
          case Success(id) => shaderIds += entry._2 -> id
          case Failure(e) => return Failure(e)
        }
      }
      
      var shaderId = shaderIds(entry._2)
      glAttachShader(pId, shaderId)
    }
    
    for(entry <- attribLocations if(entry._2 != ShaderFactory.UNKNOWN_LOCATION)) 
        glBindAttribLocation(pId, entry._2, entry._1)
        
    glLinkProgram(pId)
    checkProgStatus(pId, GL_LINK_STATUS, "Program link")
    
    glValidateProgram(pId)
    checkProgStatus(pId, GL_VALIDATE_STATUS, "Program validation")
    
    for(id <- shaderIds.values)
      glDetachShader(pId, id)
      
    for(name <- attribLocations.keys)
      attribLocations += name -> glGetAttribLocation(pId, name)
      
    for(name <- uniformLocations.keys)
      uniformLocations += name -> glGetUniformLocation(pId, name)
      
    return Success(new ShaderProgram(pId, attribLocations, uniformLocations))
  }
  
  private def compileShader(shaderType: Int, shader: File): Try[Int] =
    TryWith(Source.fromFile(shader))(_.mkString ) match {
      case Success(shaderSource) => {
        val shaderId = glCreateShader(shaderType)
        glShaderSource(shaderId, shaderSource)
        glCompileShader(shaderId)
        
        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL11.GL_FALSE)
          return Failure(new RuntimeException(s"Shader compilation error:\n${glGetShaderInfoLog(shaderId)}"))
          
        shaderIds += shader -> shaderId
        return Success(shaderId)
      }
      
      case Failure(e) => Failure(e)
    }
  
  private def checkProgStatus(pId: Int, statusType: Int, desc: String) = {
    if(glGetProgrami(pId, statusType) == GL11.GL_FALSE)
      throw new RuntimeException(s"${desc} error:\n${glGetProgramInfoLog(pId)}")
  }
  
  def cleanUp() = {
    for(id <- shaderIds.values)
      glDeleteShader(id)
  }
}

object ShaderFactory {
  val UNKNOWN_LOCATION = -1
}