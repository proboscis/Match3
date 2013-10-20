package com.glyph.scala.lib.util.collection.list

/**
 * @author glyph
 */
trait LILO[T] extends DoubleLinkedList[T]{
  def addLast(e:T){
    val node = new Element(e)
    node.prev = mTail.prev
    node.next = mTail
    mTail.prev.next = node
    mTail.prev = node
  }
  def removeLast():T={
    val result = mTail.prev.data
    mTail.prev.prev.next = mTail
    mTail.prev = mTail.prev.prev
    result
  }
}
