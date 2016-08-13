package model

import java.nio.ByteBuffer

import math.Vec2f
import math.Vec3f
import math.VecfLike

import primitiveBuffer.PrimitiveBuffer
import primitiveBuffer.Implicits._
import util.GlConstantWrapper

object Implicits {
  implicit object FloatIsStorable extends BufferStorable[Float] {
    override def store(f: Float, b: PrimitiveBuffer[ByteBuffer, Byte]) = b.putFloat(f)
  }
  
  implicit object FloatArrayIsStorable extends BufferStorable[Array[Float]] {
    override def store(fa: Array[Float], b: PrimitiveBuffer[ByteBuffer, Byte]) = {
      b.asFloatBuffer.put(fa)
      b.position(b.position + fa.length * 4)
      b
    }
  }
  
  implicit object IntIsStorable extends BufferStorable[Int] {
    override def store(i: Int, b: PrimitiveBuffer[ByteBuffer, Byte]) = b.putInt(i)
  }
  
  implicit object IntArrayIsStorable extends BufferStorable[Array[Int]] {
    override def store(ia: Array[Int], b: PrimitiveBuffer[ByteBuffer, Byte]) = {
      b.asIntBuffer.put(ia)
      b.position(b.position + ia.length * 4)
      b
    }
  }
  
  implicit object VecfLikeIsStorable extends BufferStorable[VecfLike[_]] {
    override def store(v: VecfLike[_], b: PrimitiveBuffer[ByteBuffer, Byte]) = b.put(v.entries)
  }
  
  implicit object Vec2fIsStorable extends BufferStorable[Vec2f] {
    override def store(v: Vec2f, b: PrimitiveBuffer[ByteBuffer, Byte]) = b.put(v.entries)
  }
  
  implicit object Vec3fIsStorable extends BufferStorable[Vec3f] {
    override def store(v: Vec3f, b: PrimitiveBuffer[ByteBuffer, Byte]) = b.put(v.entries)
  }
  
  implicit class BufferOps(b: PrimitiveBuffer[ByteBuffer, Byte]) {
    def put[T: BufferStorable](e: T) = implicitly[BufferStorable[T]].store(e, b)
  }
  
  implicit def ConstantWrapper2Constant(c: GlConstantWrapper) = c.oglConstant
}