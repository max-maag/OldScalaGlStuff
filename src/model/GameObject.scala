package model

import java.nio.ByteBuffer

trait GameObject[ModelIdType] {
  def uniformData: ByteBuffer
  def modelId: ModelIdType
}