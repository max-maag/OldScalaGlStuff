package util

import org.lwjgl.opengl.GL20

class ShaderProgram(val id: Int, val attribLocations: Map[String, Int], val uniformLocations: Map[String, Int]) {
  def use() = GL20.glUseProgram(id);
  def cleanUp() = GL20.glDeleteProgram(id)
}