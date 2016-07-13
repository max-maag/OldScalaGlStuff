package math

class Vec3f(_x: Float = 0, _y: Float = 0, _z: Float = 0) extends VecfLike[Vec3f] {
  val dim = 3
  
  this(0) = _x
  this(1) = _y
  this(2) = _z
  
  def copy = new Vec3f(this(0), this(1), this(2))
  
  def x = this(0)
  def y = this(1)
  def z = this(2)
  
  def x_=(f: Float) = this(0) = f
  def y_=(f: Float) = this(1) = f
  def z_=(f: Float) = this(2) = f
}

object Vec3f {
  implicit def tup2Vec(v: (Float, Float, Float)): Vec3f = new Vec3f(v._1, v._2, v._3)
}