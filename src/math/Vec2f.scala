package math

class Vec2f(_x: Float = 0, _y: Float = 0) extends VecfLike[Vec2f] {
  // val leads to initialization problems 
  override def dim = 2
  this(0) = _x
  this(1) = _y
  
  def copy = new Vec2f(this(0), this(1))
  
  def x = this(0)
  def y = this(1)
  
  def x_=(f: Float) = this(0) = f
  def y_=(f: Float) = this(1) = f
}

object Vec2f {
  implicit def tup2Vec(v: (Float, Float)): Vec2f = new Vec2f(v._1, v._2)
}