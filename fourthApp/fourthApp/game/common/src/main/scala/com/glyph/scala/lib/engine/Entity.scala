package com.glyph.scala.lib.engine

import com.glyph.scala.lib.util.Foreach
import com.glyph.libgdx.util.ArrayStack
import com.glyph.scala.lib.event.Dispatcher
import com.glyph.scala.lib.engine.Entity.OnInitialize

/**
 * @author glyph
 */
class Entity(val pkg: EntityPackage) extends Dispatcher{
  val members = pkg.memberTable.obtain()
  lazy val children = new ArrayStack[Entity] with Foreach[Entity]

  def initialize(){
    dispatch(new OnInitialize)
  }

  def free() {
    members.clear()
    children.foreach(_.free())
    children.clear()
    pkg.addFreed(this)
  }

  def set[T: Manifest](value: T) {
    members.set(implicitly[Manifest[T]], value)
  }

  def get[T: Manifest]: T = {
    members.get(implicitly[Manifest[T]]).asInstanceOf[T]
  }

  def setI(index: Int, value: Any) {
    members.set(index, value)
  }

  def getI[T](index: Int): T = {
    members.get(index).asInstanceOf[T]
  }

  def addChild(child: Entity) {
    children.push(child)
  }

  def removeChild(child: Entity) {
    children.remove(child)
  }
  def hasI(index:Int):Boolean={
    members.elementBits.get(index)
  }
}
object Entity{
  class OnInitialize
}

