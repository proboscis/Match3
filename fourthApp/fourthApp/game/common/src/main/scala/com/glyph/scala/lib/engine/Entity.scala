package com.glyph.scala.lib.engine

import com.glyph.scala.lib.util.Foreach
import com.glyph.libgdx.util.ArrayStack

/**
 * @author glyph
 */
class Entity(val pkg: EntityPackage) {
  val members = pkg.memberTable.obtain()
  val interfaces = pkg.interfaceTable.obtain()
  lazy val children = new ArrayStack[Entity] with Foreach[Entity]

  def free() {
    members.clear()
    interfaces.clear()
    children.foreach(_.free())
    children.clear()
    pkg.addFreed(this)
  }

  def setMember[T: Manifest](value: T) {
    checkIfInterface(value)
    members.set(implicitly[Manifest[T]], value)
  }

  def getMember[T: Manifest]: T = {
    members.get(implicitly[Manifest[T]]).asInstanceOf[T]
  }

  def setInterface[T <: Interface : Manifest](value: T) {
    value.onAttached(this)
    interfaces.set(implicitly[Manifest[T]], value)
  }

  def getInterface[T <: Interface : Manifest]: T = {
    interfaces.get(implicitly[Manifest[T]]).asInstanceOf[T]
  }

  def setMemberI(index: Int, value: Any) {
    checkIfInterface(value)
    members.set(index, value)
  }

  def setInterfaceI[T <: Interface](index: Int, value: T) {
    value.onAttached(this)
    interfaces.set(index, value)
  }

  def getMemberI[T](index: Int): T = {
    members.get(index).asInstanceOf[T]
  }

  def getInterfaceI[T <: Interface](index: Int): T = {
    interfaces.get(index).asInstanceOf[T]
  }

  def addChild(child: Entity) {
    children.push(child)
  }

  def removeChild(child: Entity) {
    children.remove(child)
  }

  def hasInterface(index:Int):Boolean={
    interfaces.elementBits.get(index)
  }
  def hasMember(index:Int):Boolean={
    members.elementBits.get(index)
  }

  def checkIfInterface(value:Any){
    if(value.isInstanceOf[Interface]){
      throw new RuntimeException("passing Interface as a member!"+value.getClass.getSimpleName)
    }
  }
}

