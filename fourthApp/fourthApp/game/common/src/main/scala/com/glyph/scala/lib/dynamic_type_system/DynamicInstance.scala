package com.glyph.scala.lib.dynamic_type_system

import com.glyph.libgdx.util.ArrayBag


/**
 * @author glyph
 */
class DynamicInstance(clazz: DynamicClass){
  val members = new ArrayBag[Any]
  val memberBits = new java.util.BitSet

  def set[T: Manifest](name: String, value: T) {
    if (!clazz.typeCheck[T](name)) {
      clazz.addMember[T](name)
    }
    setUnchecked(clazz.getIndex[T](name),value)
  }

  def get[T: Manifest](name: String): T = {
    if (clazz.typeCheck[T](name)) {
      getUnchecked[T](clazz.getIndex[T](name))
    } else {
      null.asInstanceOf[T]
    }
  }

  def getUnchecked[T](index: Int): T = {
    members.get(index).asInstanceOf[T]
  }

  def setUnchecked[T](index: Int, value: T) {
    memberBits.set(index)
    members.set(index, value)
  }
}
