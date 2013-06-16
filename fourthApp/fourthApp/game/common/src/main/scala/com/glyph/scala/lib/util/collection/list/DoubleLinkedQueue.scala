package com.glyph.scala.lib.util.collection.list

/**
 * @author glyph
 */
class DoubleLinkedQueue[T] extends DoubleLinkedList[T]{
  def enqueue(e:T){
    val prev = new Element(e)
    prev.next = mTail
    prev.prev = mTail.prev
    mTail.prev.next = prev
    mTail.prev = prev
  }
  def dequeue():T={
    val result = mTail.prev.data
    mTail.prev.next = null
    mTail = mTail.prev
    result
  }
}
