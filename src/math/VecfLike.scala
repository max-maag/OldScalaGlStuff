package math

trait VecfLike[V <: VecfLike[V]] {
  def dim: Int
  val entries = Array.ofDim[Float](dim)
  
  def copy: VecfLike[V]
  
  def apply(i: Int) = entries(i)
  def update(i: Int, f: Float) = entries(i) = f
  
  def foreach(o: Float => Unit) = entries.foreach(o)
  
  def combine(v: V, o: (Float, Float) => Float) = {
    entries.indices.foreach(i => entries(i) = o(entries(i), v(i)))
    this
  }
  
  def combined(v: V, o: (Float, Float) => Float) = copy.combine(v, o)
  
  def map(o: Float => Float) = {
    entries.indices.foreach(i => entries(i) = o(entries(i)))
    this
  }
  def mapped(o: Float => Float) = copy.map(o)
  
  def +(v: V) = combined(v, _+_)
  def +=(v: V) = combine(v, _+_)
  
  def -(v: V) = combined(v, _-_)
  def -=(v: V) = combine(v, _-_)
  
  def *(v: V) = combined(v, _*_)
  def *=(v: V) = combine(v, _*_)
  
  def /(v: V) = combined(v, _/_)
  def /=(v: V) = combine(v, _/_)
  
  def *(f: Float) = mapped(_ * f)
  def *=(f: Float) = map(_ * f)
  
  def dot(v: V) = entries.indices.foldLeft(0f)((c, i) => c + entries(i) * v(i))
  
  def lenSquared = entries.foldLeft(0f)((c, f) => c + f*f)
  def len = scala.math.sqrt(lenSquared)
}