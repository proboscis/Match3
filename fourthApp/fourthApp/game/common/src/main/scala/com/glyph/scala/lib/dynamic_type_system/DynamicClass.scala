package com.glyph.scala.lib.dynamic_type_system

import com.glyph.scala.lib.util.ValueIndexer
import com.glyph.scala.Glyph

/**
 * @author glyph
 */
class DynamicClass(val className: String) {
  val TAG = "[DynamicClass" + " " + className + "]"
  val memberIndexer = new ValueIndexer[String, Manifest[_]]

  def typeCheck[T: Manifest](name: String): Boolean = {
    val typ = memberIndexer.getValue(name)
    if (typ == implicitly[Manifest[T]]) {
      true
    } else if (typ == null) {
      false
    } else {
      throw new RuntimeException(TAG + ": type mismatch! at " + name + ". original = " + typ.runtimeClass.getSimpleName + ", target = " + implicitly[Manifest[T]].runtimeClass.getSimpleName)
    }
  }

  def addMember[T: Manifest](name: String): Unit = {
    if (typeCheck[T](name)) {
      throw new RuntimeException(TAG + ": member with name " + name + " is already defined with " + memberIndexer.getValue(name))
    } else {
      addMemberNoCheck[T](name)
    }
  }

  private def addMemberNoCheck[T: Manifest](name: String): Int = {
    Glyph.log(TAG, "addMember " + name + " as " + implicitly[Manifest[T]].runtimeClass.getSimpleName)
    memberIndexer.newIndex(name, implicitly[Manifest[T]])
  }

  def getIndex[T: Manifest](name: String): Int = {
    typeCheck[T](name)
    val result = memberIndexer.getIndex(name)
    if (result == null) {
      addMemberNoCheck[T](name)
    } else {
      result
    }
  }
}
