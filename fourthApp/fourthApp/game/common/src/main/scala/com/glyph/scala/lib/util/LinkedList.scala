package com.glyph.scala.lib.util

import com.badlogic.gdx.utils.Pool

/**
 * @author glyph
 */
class LinkedList [T]extends Traversable[T]{
  val mHead = new Element
  private val iteratorPool = new IteratorPool
  private val elementPool = new ElementPool


  def push(e:T){
    val next = elementPool.obtain()
    next.data = e
    next.next = mHead.next
    mHead.next = next
  }
  def pop():T={
    val result = mHead.next.data
    mHead.next = mHead.next.next
    result
  }


  def remove(t:T){
    var current = mHead.next
    var prev = mHead
    var continue = current.next != null
    while(continue){
      if (current.data == t){
        prev.next = current.next
        current.next = null//let gc delete this entity
        current.data = null.asInstanceOf[T]
        elementPool.free(current)
        continue = false
      }else{
        prev = current
        current = current.next
      }
    }
  }

  def foreach[U](f: (T) => U) {
    var current = mHead
    while(current != null && current.next != null){
      current = current.next
      f(current.data)
    }
  }

  class Element{
    var data:T = null.asInstanceOf[T]
    var next:Element = null
  }
  def iterator=iteratorPool.obtain()

  class Iterator{
    var current:Element = null
    def init(){current = mHead.next}
    init()
    def hasNext():Boolean = current.next != null
    def next():T={
      val data = current.data
      current = current.next
      data
    }
  }


  override def isEmpty: Boolean = {
    mHead.next == null
  }

  class IteratorPool extends Pool[Iterator]{
    def newObject() = new Iterator
  }
  class ElementPool extends Pool[Element]{
    def newObject() = new Element
  }
}
