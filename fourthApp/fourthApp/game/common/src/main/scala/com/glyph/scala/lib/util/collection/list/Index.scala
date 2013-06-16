package com.glyph.scala.lib.util.collection.list

/**
 * @author glyph
 */
trait Index[T] extends DoubleLinkedList[T]{
  def removeAt(index:Int):T={
    var i = 0
    var current = mHead.next
    var continue = true
    var result :T= null.asInstanceOf[T]
    while (current.data != null && continue) {
      if (i == index) {
        result = current.data
        current.prev.next = current.next
        current.next.prev = current.prev
        continue = false
      } else {
        current = current.next
        i += 1
      }
    }
    result
  }
}
