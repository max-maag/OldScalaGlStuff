package model

import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL44._
import org.lwjgl.opengl.GL45._
import util.GlUtils
import java.nio.ByteBuffer
import Implicits._
import primitiveBuffer.Implicits._
import primitiveBuffer.PrimitiveBuffer

class GlPersistentBuffer(size: Long) {
  val id = glCreateBuffers()
  
  private val flags =
    GL_MAP_PERSISTENT_BIT |
    GL_MAP_COHERENT_BIT |
    GL_MAP_WRITE_BIT
    
  glNamedBufferStorage(id, size, flags)
  
  val buffer: PrimitiveBuffer[ByteBuffer, Byte] = glMapNamedBufferRange(id, 0, size, flags)
  
  def cleanUp() = {
    glUnmapNamedBuffer(id)
    glDeleteBuffers(id)
  }
}