package com.glyph.scala.lib.util.collection

/**
 * @author glyph
 */
trait DoubleTree[T<:DoubleTree[T]] extends Tree[T]{
  var parent:DoubleTree[T] = null

  override def addChild(child:T) {
    child.parent = this
    super.addChild(child)
  }

  override def removeChild(child: T) {
    child.parent = null
    super.removeChild(child)
  }
}
