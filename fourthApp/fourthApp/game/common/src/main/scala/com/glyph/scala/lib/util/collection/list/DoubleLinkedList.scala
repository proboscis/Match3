package com.glyph.scala.lib.util.collection.list

/**
 * @author glyph
 */
class DoubleLinkedList[T] extends Traversable[T] {
  var mHead = new Element(null.asInstanceOf[T])
  var mTail = new Element(null.asInstanceOf[T])
  mHead.next = mTail
  mTail.prev = mHead


  override def last: T = mTail.prev.data

  override def head: T = mHead.next.data

  def push(e: T) {
    val next = new Element(e)
    next.next = mHead.next
    next.prev = mHead
    mHead.next.prev = next
    mHead.next = next
  }

  def pop(): T = {
    val result = mHead.next.data
    mHead.next.next.prev = mHead
    mHead.next = mHead.next.next
    result
  }

  def remove(e: T) {
    var current = mHead.next
    var continue = true
    while (current.data != null && continue) {
      if (e == current.data) {
        current.prev.next = current.next
        current.next.prev = current.prev
        continue = false
      } else {
        current = current.next
      }
    }
  }


  override def isEmpty: Boolean = {
    mHead.next == mTail
  }

  def foreach[U](f: (T) => U) {
    var current = mHead.next
    while (current != mTail) {
      f(current.data)
      current = current.next
    }
  }

  class Element(val data: T) {
    var next: Element = null
    var prev: Element = null
  }

}
