package model

import math.Vec2f
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.BufferUtils

class Rect(val lo: Vec2f, val hi: Vec2f) {
  def this(center: Vec2f, width: Float, height: Float) =
    this(center - (width/2, height/2), center + (width/2, height/2))
  
  val vaoId = glGenVertexArrays()
  val vboId = glGenBuffers()
  
  glBindVertexArray(vaoId)
  glBindBuffer(GL_ARRAY_BUFFER, vboId)
  glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
  glBindBuffer(GL_ARRAY_BUFFER, 0)
  glBindVertexArray(0)
  
  val vertBuffer = BufferUtils.createFloatBuffer(8)
  
  def render(): Unit = {
    vertBuffer.clear()
    vertBuffer
      .put(hi.x).put(lo.y)
      .put(hi.x).put(hi.y)
      .put(lo.x).put(lo.y)
      .put(lo.x).put(hi.y)
      .flip()
    
    glBindBuffer(GL_ARRAY_BUFFER, vboId)
    glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
    
    glBindVertexArray(vaoId)
    glEnableVertexAttribArray(0)
    
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    
    glDisableVertexAttribArray(0)
    glBindVertexArray(0)
  }
    
  def cleanUp(): Unit = {
    glDeleteBuffers(vboId)
    glDeleteVertexArrays(vaoId)
  }
}