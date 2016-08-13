package model

import org.lwjgl.opengl.GL11._
import util.GlConstantWrapper

trait NumType extends GlConstantWrapper {
  /** In bytes */
  val size: Int
}

case object UnsignedByte extends NumType {
  override val oglConstant = GL_UNSIGNED_BYTE
  override val size = 1
}

case object UnsignedShort extends NumType {
  override val oglConstant = GL_UNSIGNED_SHORT
  override val size = 2
}

case object UnsignedInt extends NumType {
  override val oglConstant = GL_UNSIGNED_INT
  override val size = 4
}