package com.glyph.scala.lib.util.collection

/**
 * @author glyph
 */
class DoubleLinkedList[T] extends Traversable[T]{
  var mHead = new Element(null.asInstanceOf[T])
  var mTail = mHead

  def push(e:T){
    mHead.prev = new Element(e)
    mHead.prev.next = mHead
    mHead = mHead.prev
  }

  def pop():T={
    val result = mHead.data
    mHead.next.prev = null
    mHead = mHead.next
    result
  }

  def remove(e:T){
    var head = mHead
    var continue = true
    while (head.data != null && head.next != null && continue){
      if(e == head.data){
        if (head.prev != null){
          head.prev.next = head.next
        }
        if (head.next != null){
          head.next.prev = head.prev
        }
        continue = false
      }else{
        head = head.next
      }
    }
  }


  override def isEmpty: Boolean = {
    mHead.data == null | mHead.next == null
  }

  def foreach[U](f: (T) => U) {
    var head = mHead
    while (head.data != null && head.next != null){
      f(head.data)
      head = head.next
    }
  }

  class Element(val data:T){
    var next:Element = null
    var prev:Element = null
  }
}
