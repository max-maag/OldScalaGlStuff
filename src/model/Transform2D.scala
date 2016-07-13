package model

import math.Vec2f
import org.lwjgl.BufferUtils

class Transform2D[ModelIdType](
    val modelId: ModelIdType,
    position: Vec2f,
    rotation: Float,
    scale: Float) extends GameObject[ModelIdType] {
  
  def uniformData = {
    val buf = BufferUtils.createByteBuffer(36)
    val cos = scala.math.cos(rotation).toFloat
    val sin = scala.math.sin(rotation).toFloat
    
    buf.putFloat(scale*cos)
       .putFloat(scale*sin)
       .putFloat(0)
       .putFloat(-scale*sin)
       .putFloat(scale*cos)
       .putFloat(0)
       .putFloat(position.x)
       .putFloat(position.y)
       .putFloat(1)
       .rewind()
       
     buf
  }
}