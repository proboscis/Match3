package com.glyph.scala.lib.libgdx.tween

import com.glyph.scala.lib.util.updatable.task.tween.{Tween, Accessor}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import scala.reflect.ClassTag

/**
 * @author proboscis
 */
object Test extends GdxTweenOps
trait GdxTweenOps {
  type GdxX = {
    def getX():Float
    def setX(x:Float):Unit
  }
  type GdxY = {
    def getY():Float
    def setY(x:Float):Unit
  }
  trait XAccessor[T] extends Accessor[T]
  trait YAccessor[T] extends Accessor[T]
  trait XYAccessor[T] extends Accessor[T]
  implicit def reflectedXAccessor[T<:GdxX] = new XAccessor[T]{
    def size: Int = 1
    def get(tgt: T, result: Array[Float]){result(0) = tgt.getX()}
    def set(tgt: T, values: Array[Float]){tgt.setX(values(0))}
  }
  implicit def reflectedYAccessor[T<:GdxY] = new YAccessor[T] {
    def size: Int = 1
    def get(tgt: T, result: Array[Float]) = result(0) = tgt.getY()
    def set(tgt: T, values: Array[Float])= tgt.setY(values(0))
  }
  def X[T:ClassTag] = classOf[T] match{
    case c:GdxX=> implicitly[XAccessor[T]]
  }
  def Y[T] = implicitly[YAccessor[T]]
  X[Actor]
}
