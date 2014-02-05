package com.glyph._scala.lib.util.animator

trait Animating[T] extends Any {
  def get: T
  def set(t: T)
}
trait AnimatedFloat2[T]{
  def getX(tgt:T):Float
  def getY(tgt:T):Float
  def setX(tgt:T)(x:Float)
  def setY(tgt:T)(y:Float)
}



