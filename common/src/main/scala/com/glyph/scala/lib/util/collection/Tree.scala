package com.glyph.scala.lib.util.collection

/**
 * @author glyph
 */
trait Tree[T<:Tree[T]] extends Traversable[T]{
  self :T=>
  val children = new LinkedList[T]

  def addChild(child:T){
    children.push(child)
  }

  def removeChild(child:T){
    children.remove(child)
  }

  def foreach[U](f: (T) => U) {
    f(self)
    children.foreach{
       child=> {
         child.foreach(f)
       }
    }
  }
}
