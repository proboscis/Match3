package com.glyph.scala.lib.util.collection

/**
 * @author glyph
 */
trait DoubleTree[T<:DoubleTree[T]] extends Tree[T]{
  self:T=>
  var parent:T = null.asInstanceOf[T]

  override def addChild(child:T) {
    child.parent = self
    super.addChild(child)
  }

  override def removeChild(child: T) {
    child.parent = null.asInstanceOf[T]
    super.removeChild(child)
  }
}
