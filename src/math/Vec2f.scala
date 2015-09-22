package math

class Vec2f(var x: Float = 0, var y: Float = 0) {
  def this(v: Vec2f) = this(v.x, v.y)
  
  def zip(v: Vec2f, o: (Float, Float) => Float): Vec2f = {
    x = o(x, v.x)
    y = o(y, v.y)
    return this
  }
  
  def zipped(v: Vec2f, o: (Float, Float) => Float): Vec2f =
    new Vec2f(this).zip(v, o)
    
  def foreach(f: Float, o: (Float, Float) => Float): Vec2f = {
    x = o(x, f)
    y = o(y, f)
    return this
  }
  
  def foreached(f: Float, o: (Float, Float) => Float): Vec2f =
    new Vec2f(this).foreach(f, o)
    
  def +(v: Vec2f) = zipped(v, _+_)
  def +=(v: Vec2f) = zip(v, _+_)
  
  def -(v: Vec2f) = zipped(v, _-_)
  def -=(v: Vec2f) = zip(v, _-_)
  
  def *(f: Float) = foreached(f, _*_)
  def *=(f: Float) = foreach(f, _*_)
  
  def *(v: Vec2f) = x * v.x + y * v.y
  
  def lenSquared = x*x + y*y
  def len = scala.math.sqrt(lenSquared)
}

object Vec2f {
  implicit def tup2Vec(v: (Float, Float)): Vec2f = new Vec2f(v._1, v._2)
}