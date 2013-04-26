package com.glyph.scala.lib.engine

/**
 * @author glyph
 */
class Entity(val pkg: EntityPackage) {
  val members = pkg.memberTable.obtain()
  val interfaces = pkg.interfaceTable.obtain()

  def free() {
    members.clear()
    interfaces.clear()
    pkg.addFreed(this)
  }

  def setMember[T: Manifest](value: T) {
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
}
