package model

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL32._
import org.lwjgl.opengl.GL40._

trait RenderMode {
  val oglConstant: Int
}

case object Points extends RenderMode {
  override val oglConstant = GL_POINTS
}

case object LineStrip extends RenderMode {
  override val oglConstant = GL_LINE_STRIP
}

case object LineLoop extends RenderMode {
  override val oglConstant = GL_LINE_LOOP
}

case object Lines extends RenderMode {
  override val oglConstant = GL_LINES
}

case object TriangleStrip extends RenderMode {
  override val oglConstant = GL_TRIANGLE_STRIP
}

case object TriangleFan extends RenderMode {
  override val oglConstant = GL_TRIANGLE_FAN
}

case object Triangles extends RenderMode {
  override val oglConstant = GL_TRIANGLES
}

case object LinesAdjacency extends RenderMode {
  override val oglConstant = GL_LINES_ADJACENCY
}

case object LineStripAdjacency extends RenderMode {
  override val oglConstant = GL_LINE_STRIP_ADJACENCY
}

case object TrianglesAdjacency extends RenderMode {
  override val oglConstant = GL_TRIANGLES_ADJACENCY
}

case object TriangleStripAdjacency extends RenderMode {
  override val oglConstant = GL_TRIANGLE_STRIP_ADJACENCY
}

case object Patches extends RenderMode {
  override val oglConstant = GL_PATCHES
}