package com.glyph.scala.lib.util.collection


/**
 * @author glyph
 */
class TypeInstanceMap extends ArrayMap[Manifest[_],Any]{
  def get[T:Manifest]{
    get(implicitly[Manifest[T]]).asInstanceOf[T]
  }
  def set[T:Manifest](value:T){
    set(implicitly[Manifest[T]],value)
  }
}
