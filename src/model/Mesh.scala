package model

import java.nio.ByteBuffer

trait Mesh {
  val vertexData: ByteBuffer
  val id: String
}