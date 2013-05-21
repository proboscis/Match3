package com.glyph.scala.lib.util.collection

/**
 * @author glyph
 */
class DoubleLinkedQueue[T] extends DoubleLinkedList[T]{
  def enqueue(e:T){
    push(e)
  }
  def dequeue():T={
    val result = mTail.prev.data
    mTail.prev.next = null
    mTail = mTail.prev
    result
  }
}
