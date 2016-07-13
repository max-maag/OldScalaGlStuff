package util

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL33._

class ShaderProgram(val id: Int, val attributes: Map[String, ShaderArgument], val uniforms: Map[String, ShaderArgument]) {
  def use() = glUseProgram(id);
  def enableVertAttribArrays() = attributes.values.foreach { argument =>
    for(i <- 0 until argument.size)
      glEnableVertexAttribArray(argument.location + i)
  }
  
  def disableVertAttribArrays() = attributes.values.foreach { argument =>
    for(i <- 0 until argument.size)
      glDisableVertexAttribArray(argument.location + i)
  }
  
  def vertexAttribDivisor(attrib: String, divisor: Int) = {
    val argument = attributes(attrib)
    for(i <- 0 until argument.size)
      glVertexAttribDivisor(argument.location + i, divisor)
  }
  
  def cleanUp() = glDeleteProgram(id)
}