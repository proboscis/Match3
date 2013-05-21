package com.glyph.scala.lib.util.collection

import collection.mutable

/**
 * @author glyph
 */
class LinkedList[T] extends mutable.Traversable[T] {
  val mHead = new Element

  def push(e: T) {
    val next = new Element
    next.data = e
    next.next = mHead.next
    mHead.next = next
  }

  def pop(): T = {
    val result = mHead.next
    mHead.next = result.next
    val data = result.data
    data
  }


  def remove(t: T) {
    var current = mHead.next
    var prev = mHead
    var continue = current.next != null
    while (continue) {
      if (current.data == t) {
        prev.next = current.next
        continue = false
      } else {
        prev = current
        current = current.next
      }
    }
  }

  override def foreach[U](f: (T) => U) {
    var current = mHead
    while (current != null && current.next != null) {
      current = current.next
      f(current.data)
    }
  }

  def foreachPop[U](f: (T) => U) {
    while (!isEmpty) {
      f(pop())
    }
  }

  def clear() {
    while (!isEmpty) {
      pop()
    }
  }

  class Element {
    var data: T = null.asInstanceOf[T]
    var next: Element = null
  }

  override def isEmpty: Boolean = {
    mHead.next == null
  }
}
