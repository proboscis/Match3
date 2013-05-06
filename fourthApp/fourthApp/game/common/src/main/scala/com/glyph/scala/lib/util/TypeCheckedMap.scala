package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class TypeCheckedMap[K,V] extends ArrayMap[K,V]{
  def get[T<:V:Manifest](key:K):T={
    get(key).asInstanceOf[T]
  }
  def get[T<:V:Manifest](index:Int):T={
    get(index).asInstanceOf[T]
  }
}
