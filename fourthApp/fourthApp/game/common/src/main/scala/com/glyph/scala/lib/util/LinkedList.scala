package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class LinkedList [T]{
  val head = new Element(null.asInstanceOf[T])
  var tail = head
  def push(e:T){
    tail.next = new Element(e)
    tail = tail.next
  }

  def foreach(proc:T =>Unit){
    var current = head.next
    while(current.next != null){
      proc(current.data)
      current = current.next
    }
  }

  def remove(t:T){
    var current = head.next
    var prev = head
    var continue = current.next != null
    while(continue){
      if (current.data == t){
        prev.next = current.next
        current.next = null//let gc delete this entity
        continue = false
      }else{
        prev = current
        current = current.next
      }
    }
  }

  class Element(val data:T){
    var next:Element = null
  }
  def iterator=new Iterator

  class Iterator{
    def init(){current = head.next}
    var current:Element = null
    init()
    def hasNext():Boolean = current.next != null
    def next():T={
      val data = current.data
      current = current.next
      data
    }
  }
}
